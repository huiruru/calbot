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


(defn get-date-time [start-date start-time]
  (c/to-string (f/parse (string/join "T" [start-date start-time]))))


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


(defn print-event-info [title description location event-datetime end-datetime attendees]
  "prints event information to be scheduled"
  (println (string/join " " [title description location event-datetime end-datetime attendees])))


(defn schedule-goog 
  "given console params, print info and schedule event"
  [start-date start-time duration-unit duration title description location attendees]
  (let [event-datetime (get-date-time start-date start-time)
        end-datetime (obtain-end-date event-datetime duration duration-unit)]
  (println (c/to-string  event-datetime))
  (println end-datetime)
  (println (gcal/add-calendar-time-event gcredentials title description location event-datetime end-datetime attendees))
  ))


(defn list-event 
  "given a date string list events of the day. TODO: takes in diff params"
  [date]
  (println (gcal/list-events gcredentials (c/to-string (f/parse date)) (obtain-end-date date 1 "d"))))
