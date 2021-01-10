(ns quil-reframe-test.events
  (:require
   [re-frame.core :as re-frame]
   [quil-reframe-test.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))


(re-frame/reg-event-db
 :update-rate-change
 (fn [db [_ val]]
   (assoc db :update-rate val)))

(re-frame/reg-event-db
 :running?
 (fn [db _]
   (assoc db :running? (not (:running? db)))))

(re-frame/reg-event-db
 :levels
 (fn [db [_ lvls]]
   (assoc db :levels lvls)))

(re-frame/reg-event-db
 :divisions
 (fn [db [_ divs]]
   (assoc db :divisions divs)))

(re-frame/reg-event-db
 :translate-val
 (fn [db [_ val]]
   (assoc db :translate-val val)))
