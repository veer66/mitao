(ns mitao.core
    (:require ))

(enable-console-print!)

(def center {:x 250 :y 250})

(def app-state (atom {:x 100
                      :y 0
                      :angle 45}))

(def canvas (.getElementById js/document "draw"))
(def ctx (.getContext canvas "2d"))

(def tao-size {:x 10 :y 15})

(defn angle-convert [degree]
  (-> degree
      (* Math/PI)
      (/ 180.0)))

(defn rotate-x [x y pi-angle]
  (+ (* x (Math/cos pi-angle))
     (- (* y (Math/sin pi-angle)))))

(defn rotate-y [x y pi-angle]
  (+ (* x (Math/sin pi-angle))
     (* y (Math/cos pi-angle))))

(defn rotate-dot [dot pi-angle]
  {:x (rotate-x (:x dot) (:y dot) pi-angle)
   :y (rotate-y (:x dot) (:y dot) pi-angle)})

(defn draw-tao []
  (prn tao-size)
  (let [x (:x @app-state)
        y (:y @app-state)
        angle (:angle @app-state)
        dots (map #(array-map :x %1 :y %2)
                  [0 (- (:x tao-size)) (+ (:x tao-size))]
                  [(- (:y tao-size)) (+ (:y tao-size)) (+ (:y tao-size))])
        rotated-dots (map #(rotate-dot % (angle-convert angle)) dots)
        moved-rotated-dots (map #(array-map :x (+ (:x center) (:x %) x)
                                            :y (+ (:y center) (:y %) y))
                                rotated-dots)]
    (prn rotated-dots)
    (set! (.-fillStyle ctx) "rgb(255,0,0)")
    (.beginPath ctx)
    (.moveTo ctx
             (-> moved-rotated-dots first :x)
             (-> moved-rotated-dots first :y))
    (doseq [dot moved-rotated-dots]
      (.lineTo ctx (-> dot :x) (-> dot :y)))
    (.fill ctx)
    ))

(draw-tao)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
