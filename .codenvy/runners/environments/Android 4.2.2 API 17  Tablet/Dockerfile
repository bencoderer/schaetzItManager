FROM codenvy/android422
ADD $app$ /home/user/application.apk

RUN echo no | android create avd -n mytablet -t android-17 --skin WXGA800

RUN echo "[begin] (Blackbox) \n \
      [exec] (Terminal) {urxvt -fn "xft:Terminus:size=12"} \n \
      [exec] (Emulator) {emulator64-arm -avd myandroid} \n \
      [exec] (Tablet Emulator) {emulator64-arm -avd mytablet} \n \
      [exec] (Install .apk) {adb install /home/user/application.apk} \n \
   [end]" | sudo tee /etc/X11/blackbox/blackbox-menu
   
RUN echo "window.x = 10" > ~/.android/avd/mytablet.avd/emulator-user.ini