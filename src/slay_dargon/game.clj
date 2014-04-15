(ns slay-dargon.game
  (:use [slay-dargon.areas.main :only [make-main]]
        [slay-dargon.areas.north :only [make-north]]
        [slay-dargon.areas.south :only [make-south]]
        [slay-dargon.areas.dennis :only [make-dennis]]
        [slay-dargon.command :only [make-commands process-input]]
        [slay-dargon.handler :only [message score game-over]]))

(defrecord Game [location dictionary areas commands handlers game-over-commands game-over-handlers message score unknown])

; dictionary is commands that might be used in any area - and aliases
; for any commonly used words :D
(def dictionary
  (make-commands [:get -> "get" | "take"]
                 [:ye -> "ye" | "yon" | nil]
                 [:help -> "help" | "helpeth" | "look"]))

(def areas {:main (make-main)
            :north (make-north)
            :south (make-south)
            :dennis (make-dennis)})

(def commands
  (make-commands [:die -> "die"]
                 [:dance -> "dance"]
                 [:get-unknown -> :get :unknown]
                 [:get-dagger -> :get "dagger"]
                 [:go-unknown -> "go" :unknown]
                 [:look-unknown -> "look" :unknown]
                 [:talk-unknown -> "talk" :unknown]
                 [:give-unknown -> "give" :unknown]
                 [:smell -> "smell" | "sniff"]
                 [:exit -> "exit" | "quit"]))

(defn unknown
  [game unknowns]
  (message game "That does not computeth. Type HELP if thou needs of it."))

(def handlers
  {:die (fn [game unknowns]
          (-> game
              (score -100)
              (message "That wasn't very smart. Your score was "
                       (- (:score game) 100)
                       ". Play again? [Y/N]")
              (game-over)))
   :dance (fn [game unknowns]
            (message game "Thou shaketh it a little, and it feeleth all right."))
   :get-unknown (fn [game unknowns]
                  (message game "Thou cannotst get that. Quit making stuffeth up!"))
   :get-dagger (fn [game unknowns]
                 (-> game
                     (message "Yeah, okay.")
                     (score 25)))
   :go-unknown (fn [game unknowns]
                 (message game "Thou cannotst go there. Who do you think thou art, a magistrate?!"))
   :look-unknown (fn [game unknowns]
                   (message game "It looketh pretty awesome."))
   :unknown unknown
   :talk-unknown (fn [game unknowns]
                   (message game "Who is " (apply str unknowns) "? Your new boyfriend? Somebody from work you don't want me to meeteth?"))
   :give-unknown (fn [game unknowns]
                   (message game "Thou don'tst have a " (apply str unknowns) " to give. Go back to your tiny life."))
   :smell (fn [game unknowns]
            (message game "You smell a Wumpus."))
   :exit (fn [game unknowns]
           nil)})

(def game-over-commands
  (make-commands [:y -> "y" | "yes"]
                 [:n -> "n" | "no" | "exit" | "quit"]))

(declare new-game)

(def game-over-handlers
  {:y (fn [game unknowns]
        (new-game))
   :n (fn [game unknowns]
        nil)})

(defn new-game
  "initializes a new game"
  []
  ; immediately look to pre-populate the message
  (process-input "look" (->Game :main
                                dictionary
                                areas
                                commands
                                handlers
                                game-over-commands
                                game-over-handlers
                                ""
                                0
                                unknown)))
