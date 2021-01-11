# WebRosterImport
Android application to log on to SGH WebRoster website and download shift schedule to calendar.

As it stands, this project being limited to my own employee access, it basically works as a frontend for a frontend. 
The user can log on to WebRoster via the SAS intranet portal and see a summary of their shifts for a selected month. 
The real usability will come when I can implement calendar interaction to add the month's shifts to the calendar display,
which in my opinion is an easier to parse point of reference for daily life than scanning lines of tiny text on a poorly optimised non-mobile website.

Ideally this application would have direct backend access and be able to grab shifts directly from whichever database SGH uses for this,
possibly replacing the WebRoster site altogether, in which case I will need to come up with a new app name.
All SGH staff have been issued Android work phones, which is why I think this app would be useful and popular.

Features to come are: 

- Keeping a list of stored shifts, and keeping track of changes.
    It is common for shift start- and end times to be altered, or even having whole days moved. In order to be truly useful the app also needs to keep
    track of this, and update the user's calendar accordingly.
   
    
Dream features are:

- User persistence.
    User logs in to app once, then the app periodically refreshes their schedule in the background, notifying the user of any changes,
    or letting them know when new shifts are added to the calendar.

- User communication.
    Having some way for users to communicate with each other in the app, sharing their roster views with each other, helping to facilitate 
    inter-personell shift swaps.


It should also be noted that this application is very much still a work in progress and is far from being releasable as a finished product.

PNB
