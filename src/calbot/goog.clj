(ns calbot.goog
  (:require [clojure.string :as string]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.edn :as edn]
            [google-apps-clj.credentials :as credn]
            [google-apps-clj.google-calendar :as gcal]))

(def gcredentials
  "reads credentials edn file from user directory"
  (edn/read-string (slurp "/Users/huiru/google-creds.edn")))


(defn obtain-end-date 
  "given a start date string, duration number, and duration unit (mn-months, w-weeks, d-days, h-hours, m-minutes) calculate end datetime as string"
  [start-date duration duration-unit]
  (case duration-unit
    "mn" (c/to-string (t/plus (f/parse start-date) (t/months duration)))
    "w" (c/to-string (t/plus (f/parse start-date) (t/weeks duration)))
    "d" (c/to-string (t/plus (f/parse start-date) (t/days duration)))
    "h" (c/to-string (t/plus (f/parse start-date) (t/hours duration)))
    "m" (c/to-string (t/plus (f/parse start-date) (t/minutes duration)))
    start-date))


(defn event-info 
  "I don't know what I'm doing except getting user input"
  []
  (println "Enter title, then description, duration in minutes, then attendees by email (comma separated)")
  (let [title (read-line) description (read-line) duration (read-line) attendees (read-line)]
    (println (string/join " " [title description duration attendees]))))


(defn schedule-goog 
  "given start-date and start time as strings, prompt user to enter in more info and schedule event"
  [start-date start-time]
  (println (string/join ["Need more information for event scheduled on " start-date " at " start-time]))
  (event-info)
  ;(gcal/add-calendar-day-event gcredentials "test-event" "clojure-test" "home" "2017-05-30" "2017-05-31" ["huiru@chartbeat.com"])
  )


(defn list-event 
  "given a date string list events of the day. TODO: takes in diff params"
  [date]
  (println (gcal/list-events gcredentials (c/to-string (f/parse date)) (obtain-end-date date 1 "d"))))
