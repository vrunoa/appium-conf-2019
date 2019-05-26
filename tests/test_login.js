const wd = require('wd');
const sleep = require('asyncbox').sleep
const chai = require('chai');

const expect = chai.expect
let driver, res;
let endpoint = 'http://localhost:4723/wd/hub'

let caps = {
    'appPackage': 'me.vrunoa.appiumconftalk',
    'appActivity': '.LoginActivity',
    'appWaitActivity': '.LoginActivity',
    'deviceName': 'Android GoogleApi Emulator',
    'platformName': 'Android',
    'platformVersion': '8.0'
}

describe('Android Conference login tests', async () => {
    before(async () => {
        driver = await wd.promiseChainRemote(endpoint)
        res = await driver.init(caps)
        console.log(`Session ID ${res[0]}`);
    });
    after(async () => {
        await driver.quit()
    });
    it('Test login elements are disaplyed', async () => {
        let el = await driver.elementById("textUsername");
        expect(await el.isDisplayed()).to.equal(true);
        expect(await el.text()).to.equal('Username');
        el = await driver.elementById("userEditText");
        expect(await el.isDisplayed()).to.equal(true);
        el = await driver.elementById("textPassword");
        expect(await el.isDisplayed()).to.equal(true);
        expect(await el.text()).to.equal('Password');
        el = await driver.elementById("pwdEditText");
        expect(await el.isDisplayed()).to.equal(true);
        el = await driver.elementById("enterBtt");
        expect(await el.isDisplayed()).to.equal(true);
    });
    it('Test login fails on missing user or password values', async () => {
        let btt = await driver.elementById("enterBtt");
        await btt.click();
        let alertText = await driver.elementById("alertText");
        expect(await alertText.isDisplayed()).to.equal(true);
        expect(await alertText.text()).to.equal('Please complete all the fields');
        let el = await driver.elementById("userEditText");
        await el.sendKeys("admin");
        expect(await alertText.isDisplayed()).to.equal(false);
        await btt.click();
        el = await driver.elementById("pwdEditText");
        await el.sendKeys("password");
        await btt.click();
        expect(await alertText.isDisplayed()).to.equal(true);
        expect(await alertText.text()).to.equal('Please complete all the fields');
    });
    it('Test login fails on wrong user/password values', async () => {
        let btt = await driver.elementById("enterBtt");
        let alertText = await driver.elementById("alertText");
        let el = await driver.elementById("userEditText");
        await el.sendKeys("someuser");
        el = await driver.elementById("pwdEditText");
        await el.sendKeys("somepassword");
        await btt.click();
        expect(await alertText.isDisplayed()).to.equal(true);
        expect(await alertText.text()).to.equal('Wrong user and password!');
    });
    it('Test login opens MainActivity when everything is ok', async () => {
        let btt = await driver.elementById("enterBtt");
        let el = await driver.elementById("userEditText");
        await el.sendKeys("admin");
        el = await driver.elementById("pwdEditText");
        await el.sendKeys("admin");
        await btt.click();
        await sleep(500);
        let activity = await driver.getCurrentDeviceActivity();
        expect(activity).to.equal('.MainActivity');
    });
});