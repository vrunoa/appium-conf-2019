const wd = require('wd');
const sleep = require('asyncbox').sleep
const chai = require('chai');

const expect = chai.expect
let driver, res;
let endpoint = 'http://localhost:4723/wd/hub'

let caps = {
    'appPackage': 'me.vrunoa.appiumconftalk',
    'appActivity': '.PhoneActivity',
    'appWaitActivity': '.PhoneActivity',
    'deviceName': 'Android GoogleApi Emulator',
    'platformName': 'Android',
    'platformVersion': '8.0',
    'autoGrantPermissions': true,
    'noReset': true
}

describe('Android Conference PhoneCall tests', async () => {
    before(async () => {
        driver = await wd.promiseChainRemote(endpoint)
        res = await driver.init(caps)
    });
    after(async () => {
        await driver.quit()
    });
    it('test video is paused on incoming call', async () => {
        await sleep(2500);
        await driver.gsmCall('6505551212', 'call');
        await sleep(8000);
        let el = await driver.elementById('android:id/pause');
        expect(await el.isDisplayed()).to.equal(true);
    });
    it('test video continues after call ended', async () => {
        let el = await driver.elementById('android:id/pause');
        await driver.gsmCall('6505551212', 'cancel');
        await sleep(5000);
        expect(await el.isDisplayed()).to.equal(false);
        await sleep(3000);
    });
});