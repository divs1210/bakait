(ns bakait.core-test
  (:require [clojure.test :refer :all]
            [cljos.core   :refer :all]
            [bakait.core  :refer :all])
  (:import (javax.swing JFrame)
           (java.awt.event KeyEvent)))

(def dirs {:left  KeyEvent/VK_LEFT
           :right KeyEvent/VK_RIGHT})

(defclass <Clj> <Actor>
  {:speed 0
   :imgs  (map read-image ["res/clojure-icon.gif"
                           "res/clojure-icon-2.gif"
                           "res/clojure-icon-3.gif"
                           "res/clojure-icon-4.gif"])}
  {:init
   (fn [this]
     (this :super :init "clj")
     (this :add-anim :idle  [[(this :imgs first) 9999]])
     (this :add-anim :left  (for [img (this :imgs rest)] [img 5]))
     (this :add-anim :right (for [img (reverse (this :imgs rest))] [img 5]))
     (this :set-curr-anim :idle))

   :update
   (fn [this]
     (this :super :update)
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
       (do
         ((this :sprites first) :set :speed 8)
         ((this :sprites first) :set-curr-anim :right))

       (this :pressed? (dirs :left))
       (do
         ((this :sprites first) :set :speed -8)
         ((this :sprites first) :set-curr-anim :left))

       :else
       (do
         ((this :sprites first) :set :speed 0)
         ((this :sprites first) :set-curr-anim :idle))))})

(defn -main [& [not-quit]]
  (let [game  (new+ <Demo>)
        frame (new+ <Game-Window> game)]
    (when-not not-quit
      (.setDefaultCloseOperation (frame :peer) JFrame/EXIT_ON_CLOSE))
    (game :play)
    game))
