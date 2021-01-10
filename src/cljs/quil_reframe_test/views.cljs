(ns quil-reframe-test.views
  (:require
   [re-frame.core :as re-frame]
   [quil-reframe-test.subs :as subs]
   [quil.core :as q]
   [quil.middleware :as m]
   [reagent.core :as r]
   [reagent.dom :as rdom]))



(defn flower [level divs]
  (if (< 0 level)
    (do
      (q/fill (/ 200 level) (/ 255 level) 255)
      (q/ellipse 0 0 200 200)
      (doseq [a (map #(* (/ (* Math/PI 2) divs) %) (range 0,divs))]
        (do
          (q/push-matrix)
          (q/scale 0.5)
          (q/rotate a)
          (q/translate @(re-frame/subscribe [:translate-val]) 0)
          (flower (dec level) divs)
          (q/pop-matrix)))
      )))


(defn draw [{width :width height :height} state]
  (q/color-mode :hsb)
  (q/background 155)
  (q/fill 100 255 255)
  (q/translate (/ width 2) (/ height 2))
  (flower @(re-frame/subscribe [:levels]) @(re-frame/subscribe [:divisions]))
  (q/no-loop)
  )


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
           ;:update update-state
           :size [width height]
           :middleware [m/fun-mode])))
     :render
     (fn [] [:div])}))


(defn update-levels-range
  []
  [:div.levels
   "depth level: "
   [:input {:type "range" :min 1 :max 7
            :value @(re-frame/subscribe [:levels])        ;; subscribe
            :on-change #(re-frame/dispatch [:levels (-> % .-target .-value)])}]])


(defn update-translate-range
  []
  [:div.levels
   "translate: "
   [:input {:type "range" :min 400 :max 600 :step 10
            :value @(re-frame/subscribe [:translate-val])        ;; subscribe
            :on-change #(re-frame/dispatch [:translate-val (-> % .-target .-value)])}]])


(defn update-divisions-range
  []
  [:div.divisions
   "divisions: "
   [:input {:type "range" :min 1 :max 10
            :value @(re-frame/subscribe [:divisions])        ;; subscribe
            :on-change #(re-frame/dispatch [:divisions (-> % .-target .-value)])}]])


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
     [update-levels-range]
     [update-divisions-range]
     [update-translate-range]
     ]))
