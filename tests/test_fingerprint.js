const wd = require('wd');
const sleep = require('asyncbox').sleep
const chai = require('chai');

const expect = chai.expect
let driver, res;
let endpoint = 'http://localhost:4723/wd/hub'

let caps = {
    'appPackage': 'me.vrunoa.appiumconftalk',
    'appActivity': '.FingerprintLoginActivity',
    'appWaitActivity': '.FingerprintLoginActivity',
    'deviceName': 'Android GoogleApi Emulator',
    'platformName': 'Android',
    'platformVersion': '8.0',
    'autoGrantPermissions': true,
    'noReset': true
}

describe('Android Conference Fingerprint tests', async () => {
    before(async () => {
        driver = await wd.promiseChainRemote(endpoint)
        res = await driver.init(caps)
    });
    after(async () => {
        await driver.quit()
    });
    it('test show correct message on failed fingerprint', async () => {
        await driver.fingerprint(1112);
        let el = await driver.elementById("fingerprintMessage");
        expect(await el.isDisplayed()).to.equal(true);
        expect(await el.text()).to.equal("Failed to authenticate!");     
        await sleep(1500);   
    });
    it('test show correct message on correct fingerprint and opens right activity', async () => {
        await driver.fingerprint(1111);
        let el = await driver.elementById("fingerprintMessage");
        expect(await el.isDisplayed()).to.equal(true);
        expect(await el.text()).to.equal("Welcome you\'re authenticated");
        await sleep(2500);
        let activity = await driver.getCurrentDeviceActivity();
        expect(activity).to.equal('.MainActivity');
    });
});