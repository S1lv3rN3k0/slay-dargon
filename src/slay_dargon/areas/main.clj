(ns slay-dargon.areas.main
  (:use [slay-dargon.areas.core :only [->Area]]
        [slay-dargon.command :only [make-commands]]
        [slay-dargon.handler :only [message move score game-over]]))

(def commands
  (make-commands [:look-flask -> "look" :ye "flask"]
                 [:look-scroll -> "look" :ye "scroll"]
                 [:get-flask -> :get :ye "flask"]
                 [:get-scroll -> :get :ye "scroll"]
                 [:go-north -> "go" "north"]
                 [:go-south -> "go" "south"]
                 [:go-dennis -> "go" "dennis"]))

(def handlers
  {:help (fn [game unknowns]
           (message game "Ye find yeself in yon dungeon. Ye see a SCROLL. Behind ye scroll is a FLASK. Obvious exits are NORTH, SOUTH and DENNIS."
                    (when (get-in game [:areas :main :scroll-gone])
                      " There is definitely no YE SCROLL, so drop it.")))
   :look-flask (fn [game unknowns]
                 (message game "Looks like you could quaff some serious mead out of that thing."))
   :look-scroll (fn [game unknowns]
                  (if (get-in game [:areas :main :scroll-gone])
                    (message game "Ye seeth nothing wheretofore it went ZAP.")
                    (message game "Parchment, definitely parchment. I'd recognize it anywhere.")))
   :get-flask (fn [game unknowns]
                (if (< (get-in game [:areas :main :get-flask-count] 0) 3)
                  (-> game
                      (message "Ye cannot get ye FLASK. It is firmly bolted to a wall which is bolted to the rest of the dungeon which is probably bolted to a castle. Never you mind.")
                      (score 1)
                      (update-in [:areas :main :get-flask-count] (fnil inc 1)))
                  (-> game
                      (score -1000)
                      (message "Okay, okay. You unbolt yon FLASK and hold it aloft. A great shaking begins. The dungeon ceiling collapses down on you, crushing you in twain. Apparently, this was a load-bearing FLASK. Your score was: "
                               (- (:score game) 1000)
                               " Play again? [Y/N]")
                      (game-over))))
   :get-scroll (fn [game unknowns]
                 (if (get-in game [:areas :main :scroll-gone])
                   (-> game
                       (message "Ye doth suffer from memory loss. YE SCROLL is no more. Honestly.")
                       (score -1))
                   (-> game
                       (message "Ye takes of the scroll and reads of it. It doth say: BEWARE, READER OF THE SCROLL, DANGER AWAITS TO THE- The scroll disappears in thy hands with ye olde ZAP!")
                       (score 2)
                       (assoc-in [:areas :main :scroll-gone] true))))
   :go-north (fn [game unknowns]
               (move game :north))
   :go-south (fn [game unknowns]
               (move game :south))
   :go-dennis (fn [game unknowns]
                (move game :dennis))})

(defn make-main
  "creates the initial main room"
  []
  (->Area :main commands handlers))
