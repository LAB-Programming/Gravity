#Gravity
A cool gravity simulator which simulates (somewhat accurately)
gravitational forces in 2-Dimensions


##To Do

###Known Bugs:
* Wall positions don't update properly when resizing the window

###Missing features:
* More Interactability!!!!
    * Add and delete bodies
    * Move Bodies
* Better fullscreen
    * Mac Lion+ fullscreen button where available
    * F11 or similar to toggle fullscreen while running

###Future Plans:
* Should be more easily configurable
    * Every setting should be set-able without editing the source code
* Ability to configure and restart new simulation while running
* Refactor code to be better
    * STOP USING SO MANY DAMN GLOBALS
    * More classes, less God object
* Make it even more awesome in ways we don't even know yet


##Done

###Fixed bugs
* Method of closing currently doesn't work needs to be force quit.
    * Fixed
* I don't believe that G would be the same in 2D and 3D gravity
    * G^(dim/3) is the current formula, and it seems to work properly

###Completed features:
* Configure it so that you can actually make sun-planet systems
    * They seem to form on their own
