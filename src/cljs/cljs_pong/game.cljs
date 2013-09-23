(ns cljs-pong.game)

(def canvas (.createElement js/document "canvas"))
(def context (.getContext canvas "2d"))
(def animate (or (.-requestAnimationFrame js/window)
                 (.-mozRequestAnimationFrame js/window)
                 (fn [callback] (.setTimeout js/window callback (/ 1000 60)))))
(def keysDown (js/Object.))

(deftype Paddle [x y width height x-speed y-speed]
  Object
  (render [_]
    (set! (.-fillStyle context) "#0000FF")
    (.fillRect context x, y, width, height))
  (move [this dx dy]
    (set! (.-x this) (+ x dx))
    (set! (.-y this) (+ y dy))
    (set! (.-x-speed this) dx)
    (set! (.-y-speed this) dy)
    (cond
      (< x 0) (do (set! (.-x this) 0)
                  (set! (.-x-speed this) 0))
      (> (+ x width) 400) (do (set! (.-x this) (- 400 width))
                              (set! (.-x-speed this) 0)))))

(deftype Player [paddle]
  Object
  (render [_]
    (.render paddle))
  (update [_]
    (doseq [key (.keys js/Object keysDown)]
      (case (js/Number key)
        37 (.move paddle -4 0)
        39 (.move paddle 4 0)
        (.move paddle 0 0)))))

(def player (Player. (Paddle. 175 580 50 10 0 0)))

(deftype Computer [paddle]
  Object
  (render [_]
    (.render paddle))
  (update [this ball]
    (let [x-pos (.-x ball)
          diff (- (- (+ (.-x paddle) (/ (.-width paddle) 2)) x-pos))
          diff (if (and (< diff 0) (< diff -4)) -5 diff)
          diff (if (and (> diff 0) (> diff 4)) 5 diff)]
      (.move paddle diff 0)
      (cond
        (< (.-x paddle) 0)
        (set! (.-x paddle) 0)
        (> (+ (.-x paddle) (.-width paddle)) 400)
        (set! (.-x paddle) (- 400 (.-width paddle)))))))

(def computer (Computer. (Paddle. 175 10 50 10 0 0)))

(deftype Ball [x y x-speed y-speed]
  Object
  (render [_]
    (.beginPath context)
    (.arc context x y 5 (* 2 Math/PI) false)
    (set! (.-fillStyle context) "#000000")
    (.fill context))
  (update [this paddle1 paddle2]
    (set! (.-x this) (+ x-speed x))
    (set! (.-y this) (+ y-speed y))
    (let [top-x (- x 5)
          top-y (- y 5)
          bottom-x (+ x 5)
          bottom-y (+ y 5)]
      (cond
        (< top-x 0) (do (set! (.-x this) 5)
                        (set! (.-x-speed this) (- x-speed)))
        (> bottom-x 400) (do (set! (.-x this) 395)
                             (set! (.-x-speed this) (- x-speed))))
      (if (or (< y 0) (> y 600))
        (do (set! (.-x-speed this) 0)
            (set! (.-y-speed this) 3)
            (set! (.-x this) 200)
            (set! (.-y this) 300)))
      (cond
        (and (> top-y 300)
             (< top-y (+ (.-y paddle1) (.-height paddle1)))
             (> bottom-y (.-y paddle1))
             (< top-x (+ (.-x paddle1) (.-width paddle1)))
             (> bottom-x (.-x paddle1)))
        (do (set! (.-y-speed this) -3)
            (set! (.-x-speed this) (+ x-speed (/ (.-x-speed paddle1) 2)))
            (set! (.-y this) (+ y (.-y-speed this))))
        (and (< top-y (+ (.-y paddle2) (.-height paddle2)))
             (> bottom-y (.-y paddle2))
             (< top-x (+ (.-x paddle2) (.-width paddle2)))
             (> bottom-x (.-x paddle2)))
        (do (set! (.-y-speed this) 3)
            (set! (.-x-speed this) (+ x-speed (/ (.-x-speed paddle2) 2)))
            (set! (.-y this) (+ y y-speed)))))))

(def ball (Ball. 200 300 0 3))


(defn render []
  (set! (.-fillStyle context) "#FF00FF")
  (.fillRect context 0, 0, 400, 600)
  (.render player)
  (.render computer)
  (.render ball))

(defn update []
  (.update player)
  (.update computer ball)
  (.update ball (.-paddle player) (.-paddle computer)))

(defn game-loop []
  (render)
  (update)
  (animate game-loop))

(defn setup []
  (set! (.-width canvas) 400)
  (set! (.-height canvas) 600)
  (.appendChild (.-body js/document) canvas)
  (animate game-loop))

(.addEventListener js/window "keydown" (fn [event]
                                         (aset keysDown (.-keyCode event) true)))
(.addEventListener js/window "keyup" (fn [event]
                                       (js-delete keysDown (.-keyCode event))))
(set! (.-onload js/window) setup)
