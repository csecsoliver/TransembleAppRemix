# TrainsembleApp
Download: https://git.sr.ht/~csoliver/TrainsembleApp/refs (choose a version, click on the version number. if there is an apk, that's for you)
Run online: https://appetize.io/app/b_qmafmdvwank72zihrnxotdac4i

## Intro
This is a train assembly tracker app. You manage train Cars, Stations and Routes in the database, and record real life combinations of them as Sightings. This project started as a webapp for a school assignment, made together with two of my classmates. After we finished it, I got inspired to make a mobile app out of it. It is currently missing one main feature the webapp had: uploading pictures. But I'm working on that one too.

## Some workings
There are four functional tabs in the app, all of them display a listing of the specific database tables. You can add records to all of them, edit existing ones, and delete too. There is minimal validation on the form fields, but they are not enforced, and the database might enforce stricter rules. If you don't know, but really want to know them exactly, consult the pb_schema.json document, or import it into a pocketbase instance installed by setup.cjs. (Other versions of pocketbase should work, but no guarantees)

## Some other interesting stuff
This is written in jetpack compose, which really was a chore at first (and still somewhat is), because I was used to markup languages (wpf xml, html). vagonweb translates to traincarweb, and it has a module that serves an eerily similar purpose to my own app, which is why I have borrowed some images in the form of URL-s from them. They can be perfectly aligned by their bottoms, and look really nice and realistic. Props to them: https://www.vagonweb.cz

## Link to my GitHub account
https://github.com/csecsoliver
