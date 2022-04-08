P2VJ version 1.1
Copyright sesamecake
BlogÅFhttp://sesamecake.blog84.fc2.com/

P2VJ is a vectorization software which can convert rasterized image to svg format.
I think most of operating system can run it by double clicking "P2VJ.jar", but it is recommended to run on some kind of shell, because you can use more memory & see error message with it.
After the program starts, please drug & drop rasterized images such as png, jpg & so forth into the window of P2VJ. 
Then, the vectorized image will be saved in the same folder with the source images.

Since setting is saved in file named "p2vjsettings.ini", don't put file with the same name in the same directory.
From the version 0.9, P2VJ can be run with CUI.


-- GUI Mode --
posterize•••Posterize images before vectorizing. Some photos or jpeg images needs posterization before vectorization. Please check this if you want to convert those types of images.
remove anti-Aliase•••Please check it when your images have anti-Aliase.
binary alpha•••Pixels whose alpha channel value less than 64 become completely transparent and more than or equal to 64 become completely opaque.
makepng•••PNG image of vectorized image will be automatically generated.
makepdf•••PDF file of vectorized image will be generated. A dialog to comfigure some property will open after D&D.


-- CUI Mode --
java -jar P2VJ.jar -i <inputfilename> <options...>

-i <Required: input file name. If this option is not exist, GUI mode will start.>
-o <Optional: output file name. In default, svg contents will be displayed in stdout.>

-png <Optional: png file name>
-png_scale <Optional: scale of png image. The default is 1.0.>
-pdf <Optional: pdf file name>
-posterize <Optional: (int) number of colors>
-remove_aa <Optional: TRUE or FALSE. Remove antialiase or not. The default is TRUE.>
-h displays help message.(almost the same with the above.)

License
This software is free software but no warranty.
Please see "gpl-3.0.txt".

####Known Problem
I got a report from an windows 7 user that he couldn't run P2VJ.
In the case, the path to java VM was changed by some other programs.
Thus P2VJ was run correctly by changing the script of "runp2vj.bat" as follows.

"C:\Program Files (x86)\Java\jre7\bin\javaw.exe" -jar p2vj.jar
pause


"jre7" is a folder which stores java runtime programs but it is Java7 specific.
It will be  different if you are using other version of Java.

