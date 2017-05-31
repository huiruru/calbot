(ns calbot.core
    (:require [clojure.string :as string]
              [clojure.tools.cli :refer [parse-opts]]
              [clj-time.core :as t]
              [clj-time.local :as l]
              [clj-time.coerce :as c]
              [clj-time.format :as f]
              [calbot.goog :as goog])
    (:gen-class :main true))

; hack
(def iso-date-pattern (re-pattern "\\d{4}-\\d{2}-\\d{2}"))
(def iso-time-pattern (re-pattern "\\d{2}:\\d{2}"))
(def valid-duration-units #{"mn" "w" "d" "h" "m"})

; simple validation function
(defn date?
  "given date string, nil if not valid else return input"
  [date-string]
  (when (and date-string (string? date-string))
        (re-matches iso-date-pattern date-string)))


(defn time? 
  "given time string, nil if not valid else return input"
  [time-string]
  (when (and time-string (string? time-string))
        (re-matches iso-time-pattern time-string)))


(defn dunit?
  "given duration unit string, false if not valid else return input"
  [duration-unit]
  (contains? valid-duration-units duration-unit))


(def cli-options
  [["-d" "--date DATE" "The date of event to schedule or date for listing events"
    :default (l/format-local-time (t/now) :date)
    :validate [#(date? %) "Should be a date"]]
   ["-st" "--start-time TIME" "The start time of the event: 24 hr time format"
    :default (l/format-local-time (t/now) :hour-minute)
    :validate [#(time? %) "Should be a time in 24 hr format"]]
   ["-du" "--duration-unit DUNIT" "The duration unit abbrev string"
    :default "h"
    :validate [#(dunit? %) "Should be mn-months, w-weeks, d-days, h-hours, m-minutes"]]
   ["-dr" "--duration DURATION" "The duration number"
    :default 1
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x1000) "Should be a number between 0 and 30"]]
   ["-t" "--title TITLE" "Title of event"
    :default "an event"]
   ["-de" "--description DESCRIPTION" "Description of the event"
    :default "default"]
   ["-l" "--location LOCATION" "Location of the event"
    :default "home"]
   ["-a" "--attendees ATTENDEES" "Comma Separated list of emails"
    :default ""]
   ["-h" "--help"]])



(defn help [options]
  (->> ["calbot is currently a command line tool for scheduling an event on your calendar"
          ""
          "Usage: calbot [options] action"
          ""
          "Options:"
          options
                                                      ""
          "Actions:"
          "  schedule      put an event on your calendar"
          "  list          list event on your calendar"]
       (string/join \newline)))


(defn exit [status msg]
  (println msg)
  (System/exit status))


(def handlers
  {:schedule #(goog/schedule-goog (:date %) (:time %) (:dunit %) (:duration %) (:title %) (:description %) (:location %) (:attendees %))
   :list #(goog/list-event (:date %))})


(defn error-msg [errors]
  (str "There were errors processing the command line arguments\n\n"
       (string/join \newline errors)))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      ;; explicitly ask for help
      (:help options) (exit 0 (help summary))
      ;; missing arguments
      (not= (count arguments) 1) (exit 1 (help summary))
      ;; validate arguments
      errors (exit 1 (error-msg errors)))
    (let [handler ((keyword (first arguments)) handlers)]
      (if handler
        (handler options)
        (exit 0 (help summary))))))
