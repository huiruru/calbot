(ns calbot.core
    (:require [clojure.string :as string]
              [clojure.tools.cli :refer [parse-opts]]
              [clj-time.core :as t]
              [clj-time.local :as l]
              [calbot.goog :as goog])
    (:gen-class :main true))

; hack
(def iso-date-pattern (re-pattern "\\d{4}-\\d{2}-\\d{2}"))
(def iso-time-pattern (re-pattern "\\d{2}:\\d{2}"))

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


(def cli-options
      [["-d" "--date DATE" "The date of event to schedule or date for listing events"
                  :default (t/today)
                  ;;:parse-fn #(string? %)] 
                  :validate [#(date? %) "Should be a date"]]
            ["-st" "--time TIME" "The start time of the event: 24 hr time format"
                       :default (l/format-local-time (l/local-now) :hour-minute)
                       :validate [#(time? %) "Should be a time in 24 hr format"]]
               ["-h" "--help"]])


(defn help [options]
      (->> ["to_to_test is a command line tool for scheduling an event on your calendar"
                              ""
                              "Usage: to_do_test [options] action"
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
    {:schedule #(goog/schedule-goog (:date %) (:time %))
        :list #(goog/list-event (:date %))})

(defn error-msg [errors]
    (str "There were errors processing the command line arguments\n\n"
                (string/join \newline errors)))

(defn -main [& args]
    (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
         (cond
                   ;; When we explicitly ask for help
                   (:help options) (exit 0 (help summary))
                   ;; When we supply no arguments
                   (not= (count arguments) 1) (exit 1 (help summary))
                   ;; Validate arguments
                   errors (exit 1 (error-msg errors)))
          (let [handler ((keyword (first arguments)) handlers)]
                  (if handler
                            (handler options)
                            (exit 0 (help summary))))))
