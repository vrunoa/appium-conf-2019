const wd = require('wd');
const sleep = require('asyncbox').sleep
const chai = require('chai');

const expect = chai.expect
let driver, res;
let endpoint = 'http://localhost:4723/wd/hub'

let caps = {
    'appPackage': 'me.vrunoa.appiumconftalk',
    'appActivity': '.LightSensorActivity',
    'appWaitActivity': '.LightSensorActivity',
    'deviceName': 'Android GoogleApi Emulator',
    'platformName': 'Android',
    'platformVersion': '8.0',
    'autoGrantPermissions': true
}

describe('Test light sensor', async () => {
    before(async () => {
        driver = await wd.promiseChainRemote(endpoint);
        res = await driver.init(caps);
    });
    after(async () => {
        await driver.quit()
    });
    it('uses light mode on high luminance', async () => {
        await driver.lightSensor(40000);
        let container = await driver.elementById("sensorContainer");
        expect(await container.getAttribute('content-desc')).to.equal("#FFFFFF");
        let textEl = await driver.elementById("sensorText");
        expect(await textEl.getAttribute('content-desc')).to.equal("#000000");
    });
    it ('uses dark mode on Zero luminance', async () => {
        await driver.lightSensor(0);
        await sleep(500);
        let container = await driver.elementById("sensorContainer");
        expect(await container.getAttribute('content-desc')).to.equal("#000000");
        let textEl = await driver.elementById("sensorText");
        expect(await textEl.getAttribute('content-desc')).to.equal("#FFFFFF");
    });
    it('uses light mode above the minimum luminance', async () => {
        await driver.lightSensor(5001);
        await sleep(500);
        let container = await driver.elementById("sensorContainer");
        expect(await container.getAttribute('content-desc')).to.equal("#FFFFFF");
        let textEl = await driver.elementById("sensorText");
        expect(await textEl.getAttribute('content-desc')).to.equal("#000000");
    });
});