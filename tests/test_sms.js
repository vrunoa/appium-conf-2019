const wd = require('wd');
const sleep = require('asyncbox').sleep
const chai = require('chai');

const expect = chai.expect
let driver, res;
let endpoint = 'http://localhost:4723/wd/hub'

let caps = {
    'appPackage': 'me.vrunoa.appiumconftalk',
    'appActivity': '.SMSLoginActivity',
    'appWaitActivity': '.SMSLoginActivity',
    'deviceName': 'Android GoogleApi Emulator',
    'platformName': 'Android',
    'platformVersion': '8.0',
    'autoGrantPermissions': true,
    'noReset': true
}

describe('Android Conference SMS tests', async () => {
    before(async () => {
        driver = await wd.promiseChainRemote(endpoint)
        res = await driver.init(caps)
    });
    after(async () => {
        await driver.quit()
    });
    it('test code is set on SMS received', async () => {
        let phoneContainer = await driver.elementById("enterPhoneContainer");
        expect(await phoneContainer.isDisplayed()).to.equal(true);
        let el = await driver.elementById("phoneEditText");
        expect(await el.isDisplayed()).to.equal(true);
        await el.sendKeys("60512345678")
        el = await driver.elementById("confirmBtt");
        await el.click();
        let codeContainer = await driver.elementById("validCodeContainer");
        expect(await codeContainer.isDisplayed()).to.equal(true);
        await driver.sendSms('2020', '1234');
        await sleep(5000);
        el = await driver.elementById('smsCode1');
        expect(await el.text()).to.equal("1");
        el = await driver.elementById('smsCode2');
        expect(await el.text()).to.equal("2");
        el = await driver.elementById('smsCode3');
        expect(await el.text()).to.equal("3");
        el = await driver.elementById('smsCode4');
        expect(await el.text()).to.equal("4");
    });
});