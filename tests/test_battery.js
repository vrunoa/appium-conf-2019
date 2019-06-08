const wd = require('wd');
const sleep = require('asyncbox').sleep
const chai = require('chai');

const expect = chai.expect
let driver, res;
let endpoint = 'http://localhost:4723/wd/hub'

let caps = {
    'appPackage': 'me.vrunoa.appiumconftalk',
    'appActivity': '.BatteryAlarmActivity',
    'appWaitActivity': '.BatteryAlarmActivity',
    'deviceName': 'Android GoogleApi Emulator',
    'platformName': 'Android',
    'platformVersion': '8.0',
    'autoGrantPermissions': true,
    'noReset': true
}

describe('Android Conference Battery tests', async () => {
    before(async () => {
        driver = await wd.promiseChainRemote(endpoint)
        res = await driver.init(caps)
    });
    after(async () => {
        await driver.quit()
    });
    it('test battery level text is changed', async () => {
        await driver.powerCapacity(52);
        let el = await driver.elementById("batteryPercent");
        expect(await el.isDisplayed()).to.equal(true);
        expect(await el.text()).to.equal("52%");
        await sleep(1000);
    });
    it('test alarm starts on battery charged', async () => {
        await driver.powerCapacity(100);
        let el = await driver.elementById("batteryPercent");
        expect(await el.isDisplayed()).to.equal(true);
        expect(await el.text()).to.equal("100%");
        el = await driver.elementById("batteryAlarmText");
        expect(await el.isDisplayed()).to.equal(true);
        el = await driver.elementById("stopBtt");
        expect(await el.isDisplayed()).to.equal(true);
        await sleep(2500);
    });
});