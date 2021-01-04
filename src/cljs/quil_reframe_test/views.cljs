(ns quil-reframe-test.views
  (:require
   [re-frame.core :as re-frame]
   [quil-reframe-test.subs :as subs]
   [quil.core :as q]
   [quil.middleware :as m]
   [reagent.core :as r]
   [reagent.dom :as rdom]))


(defn draw [{:keys [circles]}]
  (q/background 255)
  (doseq [{[x y] :pos [r g b] :color [ex ey] :size} circles]
    (q/fill r g b)
    (q/rect x y ex ey)
    ))


(defn update-state [{:keys [width height] :as state}]
  (if (< (rand-int 100) @(re-frame/subscribe [:update-rate]))
    (update state :circles conj {:pos   [(+ 20 (rand-int (- width 40)))
                                         (+ 20 (rand-int (- height 40)))]
                                 :color (repeatedly 3 #(rand-int 250))
                                 :size (repeatedly 2 #(rand-int 40))})
    state))


(defn init [width height]
  (fn []
    {:width   width
     :height  height
     :circles []}))


(defn canvas []
  (r/create-class
    {:component-did-mount
     (fn [component]
       (let [node (rdom/dom-node component)
             width (/ (.-innerWidth js/window) 2)
             height (/ (.-innerHeight js/window) 2)]
         (q/sketch
           :host node
           :draw draw
           :setup (init width height)
           :update update-state
           :size [width height]
           :middleware [m/fun-mode])))
     :render
     (fn [] [:div])}))


(defn update-rate-range
  []
  [:div.update-rate
   "update rate: "
   [:input {:type "range" :min 0.0 :max 100.0
            :value @(re-frame/subscribe [:update-rate])        ;; subscribe
            :on-change #(re-frame/dispatch [:update-rate-change (-> % .-target .-value)])}]])


(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [:div
     [:h3 "Quil re-frame demo"]
     [:div>button
      {:on-click #(re-frame/dispatch [:running?])}
      (if @(re-frame/subscribe [:running?]) "stop" "start")]
     (when @(re-frame/subscribe [:running?])
       [canvas])]
     [update-rate-range]
     ]))
