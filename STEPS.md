# Step 1
# appium-adb /lib/tools/adb-emu-commands.js
```bash
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
# npm link
------------------------------------
# Step 2
# appium-android-driver /lib/actions/commands.js
```bash
commands.lightSensor = async function lightSensor (light) {
  if (!this.isEmulator()) {
    log.errorAndThrow('lightSensor method is only available for emulators');
  }
  await this.adb.lightSensor(light);
};
```
# npm link
------------------------------------
# Step 3
# appium-base-driver /lib/protocol/routes
```bash
'/wd/hub/session/:sessionId/appium/device/light_sensor': {
    POST: {command: 'lightSensor', payloadParams: {required: ['light']}}
},
```
# npm link
------------------------------------
# Step 4
```bash
./build appium
```
------------------------------------
# Step 5
# wd lib/commands.js
```bash
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

```bash
npm link
```
------------------------------------
# Step 6
# add the new test using lightSensor
```bash
npm link wd
```
Create test
```bash 
it('uses light mode on high luminance', async () => {
    await driver.lightSensor(40000);
    let container = await driver.elementById("sensorContainer");
    expect(await container.getAttribute('content-desc')).to.equal("#FFFFFF");
    let textEl = await driver.elementById("sensorText");
    expect(await textEl.getAttribute('content-desc')).to.equal("#000000");
});
```
