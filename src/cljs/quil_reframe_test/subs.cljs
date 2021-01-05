(ns quil-reframe-test.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))


(re-frame/reg-sub
 :update-rate
 (fn [db]
   (:update-rate db)))


(re-frame/reg-sub
 :running?
 (fn [db]
   (:running? db)))

(re-frame/reg-sub
 :levels
 (fn [{levels :levels} db] levels))

 (re-frame/reg-sub :divisions #(:divisions %))
