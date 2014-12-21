(ns bakait.core-test
  (:require [cljos.core   :refer :all]
            [bakait.core  :refer :all])
  (:import (javax.swing JFrame)
           (java.awt.event KeyEvent)))

(def dirs {:left  KeyEvent/VK_LEFT
           :right KeyEvent/VK_RIGHT})

(defclass <Clj> <Sprite>
  {:speed 0}
  {:init
   (fn [this]
     (this :super :init "clj")
     (dorun 
       (map #(this :add-img (read-image %) 5)
            ["res/clojure-icon.gif"
             "res/clojure-icon-2.gif"
             "res/clojure-icon-3.gif"
             "res/clojure-icon-4.gif"])))
   
   :advance-1-frame
   (fn [this]
     (when (or (this :speed (comp not zero?))
               (this :curr-frame (comp nil? first)))
       (this :super :advance-1-frame)))
   
   :update
   (fn [this]
     (this :move-by [(this :speed) 0]))})

(defclass <Demo> <Game>
  {}
  {:init
   (fn [this]
     (this :super :init 600 100)
     (this :add-sprite (new+ <Clj>)))
   
   :update
   (fn [this]
     (cond
       (this :pressed? (dirs :right))
       ((this :sprites first) :set :speed 8)
       
       (this :pressed? (dirs :left))
       ((this :sprites first) :set :speed -8)
       
       :else
       ((this :sprites first) :set :speed 0)))})

(defn -main [& [not-quit]]
  (let [game  (new+ <Demo>)
        frame (new+ <Game-Window> game)]
    (when-not not-quit
      (.setDefaultCloseOperation (frame :peer) JFrame/EXIT_ON_CLOSE))
    (game :play)
    game))
