FROM codenvy/android442
ADD $app$ /home/user/application.apk

RUN echo no | android create avd -n mytablet -t android-19 --skin WXGA800

RUN echo "[begin] (Blackbox) \n \
      [exec] (Terminal) {urxvt -fn "xft:Terminus:size=12"} \n \
      [exec] (Emulator) {emulator64-arm -avd myandroid} \n \
      [exec] (Tablet Emulator) {emulator64-arm -avd mytablet} \n \
      [exec] (Install .apk) {adb install /home/user/application.apk} \n \
   [end]" | sudo tee /etc/X11/blackbox/blackbox-menu