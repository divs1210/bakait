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
     (doto+ this
       (:super :init "clj")
       (:add-anim :idle  [[(this :imgs first) 9999]])
       (:add-anim :left  (for [img (-> (this :imgs) rest)]
                           [img 5]))
       (:add-anim :right (for [img (-> (this :imgs) rest reverse)]
                           [img 5]))
       (:set-curr-anim :idle)))

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
       (doto+ (this :sprites first)
         (:set :speed 8)
         (:set-curr-anim :right))

       (this :pressed? (dirs :left))
       (doto+ (this :sprites first)
         (:set :speed -8)
         (:set-curr-anim :left))

       :else
       (doto+ (this :sprites first)
         (:set :speed 0)
         (:set-curr-anim :idle))))})

(defn -main [& [not-quit]]
  (let [game  (new+ <Demo>)
        frame (new+ <Game-Window> game)]
    (when-not not-quit
      (.setDefaultCloseOperation (frame :peer) JFrame/EXIT_ON_CLOSE))
    (game :play)
    game))
