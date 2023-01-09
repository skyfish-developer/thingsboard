/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.msa.ui.tests.deviceProfileSmoke;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.thingsboard.server.msa.ui.base.AbstractDriverBaseTest;
import org.thingsboard.server.msa.ui.pages.LoginPageHelper;
import org.thingsboard.server.msa.ui.pages.ProfilesPageHelper;
import org.thingsboard.server.msa.ui.pages.SideBarMenuViewHelper;
import org.thingsboard.server.msa.ui.utils.DataProviderCredential;

import static org.thingsboard.server.msa.ui.utils.Const.TENANT_EMAIL;
import static org.thingsboard.server.msa.ui.utils.Const.TENANT_PASSWORD;
import static org.thingsboard.server.msa.ui.utils.EntityPrototypes.defaultDeviceProfile;

public class SortByNameTest extends AbstractDriverBaseTest {
    private SideBarMenuViewHelper sideBarMenuView;
    private ProfilesPageHelper profilesPage;
    private String name;

    @BeforeMethod
    public void login() {
        openLocalhost();
        new LoginPageHelper(driver).authorizationTenant();
        testRestClient.login(TENANT_EMAIL, TENANT_PASSWORD);
        sideBarMenuView = new SideBarMenuViewHelper(driver);
        profilesPage = new ProfilesPageHelper(driver);
    }

    @AfterMethod
    public void delete() {
        if (name != null) {
            testRestClient.deleteDeviseProfile(getDeviceProfileByName(name).getId());
            name = null;
        }
    }

    @Test(priority = 10, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForSort")
    @Description
    public void specialCharacterUp(String name) {
        testRestClient.postDeviceProfile(defaultDeviceProfile(name));
        this.name = name;

        sideBarMenuView.openDeviceProfiles();
        profilesPage.sortByNameBtn().click();
        profilesPage.setProfileName();

        Assert.assertEquals(profilesPage.getProfileName(), name);
    }

    @Test(priority = 20, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForAllSort")
    @Description
    public void allSortUp(String deviceProfile, String deviceProfileSymbol, String deviceProfileNumber) {
        testRestClient.postDeviceProfile(defaultDeviceProfile(deviceProfileSymbol));
        testRestClient.postDeviceProfile(defaultDeviceProfile(deviceProfile));
        testRestClient.postDeviceProfile(defaultDeviceProfile(deviceProfileNumber));

        sideBarMenuView.openDeviceProfiles();
        profilesPage.sortByNameBtn().click();
        profilesPage.setProfileName(0);
        String firstDeviceProfile = profilesPage.getProfileName();
        profilesPage.setProfileName(1);
        String secondDeviceProfile = profilesPage.getProfileName();
        profilesPage.setProfileName(2);
        String thirdDeviceProfile = profilesPage.getProfileName();

        testRestClient.deleteDeviseProfile(getDeviceProfileByName(deviceProfile).getId());
        testRestClient.deleteDeviseProfile(getDeviceProfileByName(deviceProfileNumber).getId());
        testRestClient.deleteDeviseProfile(getDeviceProfileByName(deviceProfileSymbol).getId());

        Assert.assertEquals(firstDeviceProfile, deviceProfileSymbol);
        Assert.assertEquals(secondDeviceProfile, deviceProfileNumber);
        Assert.assertEquals(thirdDeviceProfile, deviceProfile);
    }

    @Test(priority = 10, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForSort")
    @Description
    public void specialCharacterDown(String name) {
        testRestClient.postDeviceProfile(defaultDeviceProfile(name));
        this.name = name;

        sideBarMenuView.openDeviceProfiles();
        profilesPage.sortByNameDown();
        profilesPage.setProfileName(profilesPage.allEntity().size() - 1);

        Assert.assertEquals(profilesPage.getProfileName(), name);
    }

    @Test(priority = 20, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForAllSort")
    @Description
    public void allSortDown(String deviceProfile, String deviceProfileSymbol, String deviceProfileNumber) {
        testRestClient.postDeviceProfile(defaultDeviceProfile(deviceProfileSymbol));
        testRestClient.postDeviceProfile(defaultDeviceProfile(deviceProfile));
        testRestClient.postDeviceProfile(defaultDeviceProfile(deviceProfileNumber));

        sideBarMenuView.openDeviceProfiles();
        int lastIndex = profilesPage.allEntity().size() - 1;
        profilesPage.sortByNameDown();
        profilesPage.setProfileName(lastIndex);
        String firstDeviceProfile = profilesPage.getProfileName();
        profilesPage.setProfileName(lastIndex - 1);
        String secondDeviceProfile = profilesPage.getProfileName();
        profilesPage.setProfileName(lastIndex - 2);
        String thirdDeviceProfile = profilesPage.getProfileName();

        testRestClient.deleteDeviseProfile(getDeviceProfileByName(deviceProfile).getId());
        testRestClient.deleteDeviseProfile(getDeviceProfileByName(deviceProfileNumber).getId());
        testRestClient.deleteDeviseProfile(getDeviceProfileByName(deviceProfileSymbol).getId());

        Assert.assertEquals(firstDeviceProfile, deviceProfileSymbol);
        Assert.assertEquals(secondDeviceProfile, deviceProfileNumber);
        Assert.assertEquals(thirdDeviceProfile, deviceProfile);
    }
}
