google_play_services_r28/


Eclipse ADT no more support. So Google break backward compatibility with remove two classes from dx.jar.
You can easy fix it.

    Go to your sdk folder. Navigate to dx.jar from latest build-tools.
    For example build-tools\28.0.3\lib
    Open dx.jar in any zip archiver.
    I use WinRAR.
    Navigate to path com\android\dx\command inside archive.
    Here you not see files DxConsole$1.class and DxConsole.class.
    Now navigate to dx.jar for 25.0.3 or before.
    Again navigate to com\android\dx\command inside this archive.
    Here you see files DxConsole$1.class and DxConsole.class.
    Copy it from old dx.jar to new dx.jar. I just drop its from one WinRAR window to another.
