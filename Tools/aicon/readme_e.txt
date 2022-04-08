----------------------------------------------------------------------
"@icon sushi" v1.21 (Feb 04, 2007) 
                                     copyright (c) 2001-2007 by towofu

 e-mail: http://www.towofu.net/soft-e/e-about.htm
 web   : http://www.towofu.net/soft-e/
----------------------------------------------------------------------
                                      readme.txt:  Update Feb 04, 2007
----------------------------------------------------------------------

INDEXES
  1) What's this?
  2) Features
  3) System Requirements
  4) License
  5) Install & UNInstall
  6) Quick Start
  7) Hotkeys
  8) Menus
  9) Bug report
 10) Development & Thanks
----------------------------------------------------------------------


1) What's this?

  This was FREEWARE convert Bitmap(*.BMP) and Icon(*.ICON) each other.
  
   ...but now, some other image formats are available.
  
  - Import formats list (up to size of 256x256)
    BMP - Windows Bitmap (1/4/8/24bit, Windows Bitmap, non compressed)
    ICO (1/4/8/24/32bit)
    PNG (1/4/8/24/32bit)
    PSD (32bit = R G B Alpha channels, No Layer style, 
         non compressed/RLE compressed)
    EXE/DLL/ICL (16/32bit) of ICO (Group icon has been supported since v1.13)
  
  - Export formats list (up to size of 256x256)
    BMP (1/4/8/24bit, Windows Bitmap, non compressed)
    ICO (1/4/8/24/32bit)
    PNG (1/4/8/24/32bit)
    ICL (16bit) of ICO (Group icon has been supported since v1.13)


2) Features

  You can...
  - Make Windows Vista format icons.
  - Make Windows XP Icons. (By opening Photoshop/PNG files. By Alpha 
    channel Editing tools.)
  - Make Multiple-Icon which has some icons in a file.
  - Edit Transparency-Mask.
  - Open 1x1 to 256x256 size of images.
  - Open 1/4/8/24/32bits color images.
  - Extract icons from EXE/DLLs. - Group icon has been supported since v1.13.
  - Import/Export Icon Library (*.ICL) - Group icon has been supported since v1.13.
  - Copy to / Paste from Clipboard.
  - Take screenshot of icon list.


3) System Requirements

  - Tested on WinXP
  - It is reported working on 98/ME/2000/NT.
  - Need 16bit or higher system color depth.


4) License

  - There is no warranty for this software.
  - You can NOT distribute this software without permission.
    Please download from formal sites.


5) Install & UNInstall

  Install:
  - Extract these files into a directory of your choise.
     \aicon.exe  (Main program file)
     \readme_e.txt (This file you're reading now)
     \readme_j.txt (Readme of japanese version)
     \brushes    (To edit alpha channel)
     \Languages   (Language file folder)
  
  Uninstall:
  - If you used INSTALLER to install this software, you MUST uninstall 
    from startmenu.
  
  - This software does NOT touch win-registry, so you can UN-Install 
    this software by deleting all files above and "aicon.ini" - 
    automatically created - in chosen directory.


6) Quick Start

  - You can use main functions by popupmenu (Right click on icon list).
  - Drag&Drop files to program window or program icon to open files.
  - Press +/- keys then you can change Zoom size, 
    and other Hotkeys are also useful.


7) Hotkeys


  * Main Window
    Ctrl + O           Open Image file(s)
    Ctrl + S           Save as Single icon(s)
    Ctrl + M           Save as Multiple icon
    Ctrl + I           Save as Icon Library
    Ctrl + B           Save as Bitmap file(s)
    Ctrl + G           Save as PNG file(s)
    F5                 Reopen selected item if Bit
    Enter              Edit Transparency Mask
    Shift + Enter      Edit Alpha Channel
    Ctrl + C           Copy to Clipboard
    Ctrl + V           Paste from Clipboard (as Ne
    Ctrl + Shift + V   Paste from Clipboard (Over 
    Ctrl + Up          Move Up selected item
    Ctrl + Dwon        Move Down selected item
    Ctrl + A           Select all items
    Del                Delete selected item(s)
    F2                 Edit Icon name
    Ctrl + L           Change Background color
    +                  Zoom In
    -                  Zoom Out
    1-5                Set Zoom size (100% - 500%)
    Ctrl + 1           Report View
    Ctrl + 2           Icon View
    Ctrl + 3           Library View
    F4                 Next View Mode
    Shift + F4         Prev View Mode
    F7                 Group selected icons.
    F8                 Disband selected group.
    Shift + Del        Delete selected group.
    Alt + Up           Move Up selected group.
    Alt + Down         Move Down selected group.
    
    *For advanced users only, Hotkeys of main window can be customized by 
     editing aicon.ini.

  * Edit Transparency Mask window
    Ctrl + Z           Undo
    +                  Zoom In
    -                  Zoom Out
    Enter              Save and Exit
    Esc                Exit Discarding Changes

  * Edit Alpha channel window
    Ctrl + Z           Undo
    +                  Zoom In
    -                  Zoom Out
    Enter              Save and Exit
    Esc                Exit Discarding Changes


8) Menus

  There are explanations for some menus.
  If you have been confused, read here.

  * File
    - Open
      Open source files. Also Drag&Drop is supported for this function.
    
    - Save as Single Icon(s)
      Save selected icons to individual icon files.
    
    - Save as Multiple Icon
      Save selected icons to an icon file.
    
    - Save as Icon Library
      Save ALL of icons to an icon library.
      First icon's name of each group must be used for resource name of ICL.
    
    - Reopen File
      Reload source files. Only for PNG/BMP/PSD.

  * Edit
    - Delete Alpha Channel
      Remove Alpah channel then convert to 24bit icon.
    
    - Copy
      Copy image to clipboard. Mask and Alpha will be ignored.
    
    - Paste (Over write)
      Over write to selected image. Mask and Alpha will not be changed.
      Width and height must be same as target image.

  * List
    - Up/Down
      Change order of icon. If icon is grouped, it can not be moved over group.
    
    - Library Edit
      Change to Group editing mode.
    
    - Group selected
      Group selected icons.
      Continuous items must be selected to do this.
    
    - Disband this group
      Disband group(s) of selected icon(s).

  * Options
    - BG color when save
      > Fill with Black
        Fill transparent pixels with Black. This is recommended setting.
        If you choose other, icon may be drawn incorrectly with some PC.
        If icon is index color and palette does not have back, 
        this will be ignored.
      > Fill with current BG Color
        Fill transparent pixels with current preview color.
        If icon is index color and palette does not have the color, 
        nearest color will be used.
      > do Nothing
        Any pixels must not be changed.
    
    - PSD Opening method
      > Each Layer
        Open each layer as individual icon.
      > Merged Image
        Open merged image as an icon.


9) Bug report

  - If you provide translations, please post to following place.
    http://www.towofu.net/cgi-bin/c-board-e/c-board.cgi


10) Development & Thanks

  - @icon sushi is developed by Borland Delphi 6 Professional.
    And I use following Components/Libraries, 
    I am thankful to authors.
  
    :GLDPNG (PNG Library) / Tarquin
     GLDPNG (c) 1998-2001 Copyright Tarquin All Rights Reserved.
     ZLIB ver 1.1.3 (c) 1995-1996 Copyright Jean-loup Gailly and Mark Adler
  
    :SHBrowseForFolderEx (Folder selection dialog) / HISHIAMAZON
     SHBrowseForFolderEx Version 2.01 ( Win95 & NT4.0)
     TSHBrowseForFolderEx component  version 2.01
     (c)1997 HISHIAMAZON All Rights Reserved.
  
  - I thank you for people who helped me by bug reporting, suggestions, 
    translations and so on.
  
----------------------------------------------------------------------
