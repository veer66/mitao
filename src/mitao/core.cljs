(ns mitao.core
    (:require ))

(enable-console-print!)

(def canvas (.getElementById js/document "draw"))
(def ctx (.getContext canvas "2d"))

(def center {:x (/ (.-width canvas) 2)  
             :y (/ (.-height canvas) 2)})

(def line-width 5)
(def line-color "#22AA22")
(def app-state (atom {:x 40
                      :y 40
                      :angle 90
                      :prev-dots [{:x 0 :y 0} {:x 40 :y 40}]}))

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

(defn move-to-center [dot]
  (array-map :x (+ (:x center) (:x dot))
             :y (+ (:y center) (:y dot))))

(defn draw-tao []
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
    (set! (.-fillStyle ctx) "rgb(255,0,0)")
    (.beginPath ctx)
    (.moveTo ctx
             (-> moved-rotated-dots first :x)
             (-> moved-rotated-dots first :y))
    (doseq [dot moved-rotated-dots]
      (.lineTo ctx (-> dot :x) (-> dot :y)))
    (.fill ctx)))

(defn draw-path []
  (let [moved-prev-dots (map move-to-center (-> @app-state :prev-dots))]
    (set! (.-strokeStyle ctx) line-color)
    (set! (.-lineWidth ctx) line-width)
    (.beginPath ctx)
    (.moveTo ctx
             (-> moved-prev-dots first :x)
             (-> moved-prev-dots first :y))
    (doseq [dot (-> moved-prev-dots rest)]
      (.lineTo ctx (:x dot) (:y dot)))
    (.stroke ctx)))

(defn reset-canvas []
  (.clearRect ctx 0 0 (.-width canvas) (.-height canvas)))

(defn draw []
  (reset-canvas)
  (draw-path)
  (draw-tao))

(draw)

(defn on-js-reload []
  (draw))
