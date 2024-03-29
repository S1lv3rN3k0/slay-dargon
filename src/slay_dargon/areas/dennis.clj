(ns slay-dargon.areas.dennis
  (:use [slay-dargon.areas.core :only [->Area]]
        [slay-dargon.command :only [make-commands]]
        [slay-dargon.handler :only [message move score game-over]]))

(def commands
  (make-commands [:not-dennis -> "not" "dennis"]
                 [:talk -> "talk" | "talk" "dennis"]
                 [:look-dennis -> "look" "dennis"]
                 [:look-jimberjam -> "look" "jimberjam"]
                 [:give-trinket -> "give" "trinket" | "give" "trinket" "to" "dennis"]))

(def handlers
  {:help (fn [game unknowns]
           (message game "Ye arrive at Dennis. He wears a sport frock coat and a long jimberjam. He paces about nervously. Obvious exits are NOT DENNIS."))
   :not-dennis (fn [game unknowns]
                 (move game :main))
   :talk (fn [game unknowns]
           (message game "You engage Dennis in leisurely discussion. Ye learns that his jimberjam was purchased on sale at a discount market and that he enjoys pacing about nervously. You become bored and begin thinking about parapets."))
   :look-dennis (fn [game unknowns]
                  (message game "That jimberjam really makes the outfit."))
   :look-jimberjam (fn [game unknowns]
                     (message game "Man, that art a nice jimberjam."))
   :give-trinket (fn [game unknowns]
                   (if (get-in game [:areas :south :get-trinket-count])
                     (-> game
                         (message "A novel idea! You givst the TRINKET to Dennis and he happily agrees to tell you what parapets are. With this new knowledge, ye escapes from yon dungeon in order to search for new dungeons and to remain... THY DUNGEONMAN!! You hath won! Congraturation!! Your score was: " (:score game))
                         (game-over))
                     (message game "Thou don'tst have a trinket to give. Go back to your tiny life.")))})

(defn make-dennis
  "makes the dennis... room"
  []
  (->Area :dennis commands handlers))
