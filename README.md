# AppiumConf 2019

<img src="https://github.com/vrunoa/appium-conf-2019/blob/updating-readme/conf-screen.png" width="640" />
https://slides.com/vrunoa/appium-conf-2019#/

https://www.youtube.com/watch?v=8Ph50Sm3Cdk

## Setup
```
git clone git@github.com:vrunoa/appium-conf-2019.git
cd appium-conf-2019.git
npm install
```

## Examples
```
npm run test-sms
npm run test-phonecall
npm run test-battery
npm run test-fingerprint
```

## Quick adding light sensor support to Appium on Android Emulators

**Step 1.** Play around with the adb command-line

```adb emu sensor set light 0```

**Step 2.** Add the method calling this command-line in appium-adb
In the appium-adb folder open `/lib/tools/adb-emu-commands.js`
and add the new method
```
/**
 * Emulate light sensor on the connected emulator.
 *
 * @param {float} light 
 */
emuMethods.lightSensor = async function lightSensor (light = 0) {
  // light sensor <light> allows you to set the ligth sensor values on the emulator.
  await this.adbExecEmu(['sensor', 'set', 'light', light]);
};
```

**Step 3.** use this new appium-adb method from the appium-android-driver. In `/lib/command/actions.js ` on the appium-android-driver folder add
```
commands.lightSensor = async function lightSensor (light) {
  if (!this.isEmulator()) {
    log.errorAndThrow('lightSensor method is only available for emulators');
  }
  await this.adb.lightSensor(light);
};
```
https://slides.com/vrunoa/appium-conf-2019#/15

**Step 4.** Adding a new endpoint; on the appium-base-driver open ` /lib/protocol/routes.js`

```
'/wd/hub/session/:sessionId/appium/device/light_sensor': {
    POST: {command: 'lightSensor', payloadParams: {required: ['light']}}
},
```
https://slides.com/vrunoa/appium-conf-2019#/17

**Step 5.** Updating the appium client. Open ` lib/commands.js` in the wd folder add:
```
/**
 * lightSensor(light, cb) -> cb(err)
 *
 * @jsonWire POST /session/:sessionId/appium/device/light_sensor
 */
commands.lightSensor = function() {
  var fargs = utils.varargs(arguments);
  var cb = fargs.callback,
      light = fargs.all[0];
  var data = {light: light};
  this._jsonWireCall({
    method: 'POST'
    , relPath: '/appium/device/light_sensor'
    , data: data
    , cb: simpleCallback(cb)
  });
};
```
https://slides.com/vrunoa/appium-conf-2019#/19

**Step 6.** Build appium and wd client
```
npm run build-appium
```

**Step 7.** Run the tests using light sensor support
https://slides.com/vrunoa/appium-conf-2019#/20
```
npm run test-light-sensor
```

Quick overview on adding a new emulator feature
