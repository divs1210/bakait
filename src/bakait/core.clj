(ns bakait.core
  (:use [cljos.core])
  (:import (java.awt Color Dimension Image)
           (javax.swing JPanel JFrame Timer JOptionPane)
           (java.awt.event ActionListener KeyListener KeyEvent)))

;------------------------------
;Helper functions
;------------------------------
(defn read-image [path]
  (javax.imageio.ImageIO/read (java.io.File. path)))

;------------------------------
;Core - Animation classes
;------------------------------
(defclass <Sprite> <Obj>
  {:name "", :pos [0 0], :img-frame-queue [], :curr-frame [nil 0]}
  {:init 
   (fn [this name]
     (this :set :name name))
    
   :move-by
   (fn [this pos]
     (this :setf :pos #(map + pos %)))
   
   :add-img
   (fn [this img & [frames]]
     (this :setf :img-frame-queue conj [img (or frames 9999)]))
   
   :advance-1-frame
   (fn [this]
     (if (> (this :curr-frame second) 0)
       (this :setf :curr-frame (fn [[img ttl]]
                                 [img (dec ttl)]))
       (doto+ this
         (:set :curr-frame (this :img-frame-queue first))
         (:setf :img-frame-queue conj (this :img-frame-queue first))
         (:setf :img-frame-queue (comp vec rest)))))
   
   :update
   (fn [this])
   
   :draw
   (fn [this graphics]
     (this :advance-1-frame)
     (.drawImage graphics (this :curr-frame first)
                          (this :pos first)
                          (this :pos second)
                          nil))})

(defclass <Actor> <Sprite>
  {:anims []}
  {:add-anim
   (fn [this anim]
     (this :setf :anims conj anim))
   
   :set-curr-anim
   (fn [this anim-index]
     (this :set :img-frame-queue (vec ((this :anims) anim-index)))
     (this :set :curr-frame (this :img-frame-queue first)))})

;------------------------------
;View - Pane and Window
;------------------------------
(defclass <Game> <Obj>
  {:sprites [], :peer nil, :pressed #{}, :timer nil}
  {:init
   (fn [self width height]
     (self :set :peer
       (proxy [JPanel ActionListener KeyListener] [true]
         (paintComponent [g]
           (proxy-super paintComponent g)
           (dorun (map #(% :draw g) (self :sprites)))
           (self :update)
           (dorun (map #(% :update) (self :sprites))))

         (actionPerformed [e]
           (.repaint this))

         (getPreferredSize []
           (Dimension. width height))
         
         (keyTyped [e])
         
         (keyPressed [e]
           (self :setf :pressed conj (.getKeyCode e)))
         
         (keyReleased [e]
           (self :setf :pressed (fn [keys]
                                  (set (remove #(= (.getKeyCode e) %) 
                                               keys)))))))
     (doto (self :peer)
       (.setFocusable true)
       (.addKeyListener (self :peer))
       (.setBackground Color/WHITE))
     
     (self :set :timer (Timer. 20 (self :peer))))
   
   :play 
   (fn [this]
     (.start (this :timer)))
   
   :update 
   (fn [this])
   
   :pressed?
   (fn [this key]
     (this :pressed contains? key))
   
   :move-by
   (fn [this sprite-name diff]
     ((first (filter #(= sprite-name (% :name)) (this :sprites))) 
       :move-by diff))
   
   :add-sprite
   (fn [this sprite]
     (this :setf :sprites conj sprite))})

(defclass <Game-Window> <Obj>
  {:peer nil}
  {:init
   (fn [this game]
     (this :set :peer (JFrame. "CljOS Game Engine"))
     (doto (this :peer)
       (.setResizable false)
       (.setAlwaysOnTop true)
       (.add (game :peer))
       .pack
       (.setVisible true)))})