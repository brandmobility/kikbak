package com.kikbak.client.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.kikbak.KikbakTest;
import com.kikbak.client.service.v1.FbLoginException;
import com.kikbak.client.service.v1.FbLoginService;
import com.kikbak.client.service.v1.FbUserLimitException;
import com.kikbak.client.service.v1.UserService;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dao.ReadWriteUserDAO;
import com.kikbak.dao.enums.GenderType;
import com.kikbak.dto.User;
import com.kikbak.jaxb.v1.register.UserType;

public class UserService_RegisterTest extends KikbakTest {

    @Autowired
    UserService userService;

    @Autowired
    ReadOnlyUserDAO roUserDao;

    @Autowired
    ReadWriteUserDAO rwUserDao;

    @Before
    public void setUp() throws Exception {
        setupDb("<dataset><user/></dataset>"); // empty user table
    }

    private static final String NAME_A = "adam";
    private static final String EMAIL_A = "adam@gmail.com";
    private static final String PHONE_A = "1234567890";

    private static final String NAME_B = "bob";
    private static final String EMAIL_B = "bob@gmail.com";
    private static final String PHONE_B = "2345678901";

    @Test
    public void testRegisterWebUser() {
        long id = userService.registerWebUser(NAME_A, EMAIL_A, PHONE_A);
        User u = roUserDao.findById(id);
        assertTrue(u != null);
        User v = roUserDao.findByManualPhoneNotFb(PHONE_A);
        assertTrue(v != null);
        assertTrue(v.getId() == id);
    }

    @Test
    public void testReRegistration() {
        long firstId = userService.registerWebUser(NAME_A, EMAIL_A, PHONE_A);
        long secondId = userService.registerWebUser(NAME_A, EMAIL_A, PHONE_A);
        assertEquals(firstId, secondId);
    }

    @Test
    public void testReRegistrationWithNewName() {
        long firstId = userService.registerWebUser(NAME_A, EMAIL_A, PHONE_A);
        long secondId = userService.registerWebUser(NAME_B, EMAIL_B, PHONE_A);
        assertEquals(firstId, secondId);
        User u = roUserDao.findById(firstId);
        assertEquals(NAME_B, u.getManualName());
        assertEquals(EMAIL_B, u.getManualEmail());
    }

    @Test
    public void testManyRegistration() {
        long idA = userService.registerWebUser(NAME_A, EMAIL_A, PHONE_A);
        long idB = userService.registerWebUser(NAME_B, EMAIL_B, PHONE_B);
        assertTrue(idA != idB);
        assertNotNull(roUserDao.findById(idA));
        assertNotNull(roUserDao.findById(idB));
    }

    @Test
    public void testCanRegisterWebAndFbWithSameNumber() {
        User fbUser = new User();
        fbUser.setFirstName("first");
        fbUser.setLastName("last");
        fbUser.setEmail("email@email.il");
        fbUser.setFacebookId(1L);
        fbUser.setGender((byte) GenderType.male.ordinal());
        fbUser.setCreateDate(new Date());
        fbUser.setUpdateDate(new Date());
        fbUser.setManualNumber(PHONE_A);

        // save fb user with phone number
        rwUserDao.makePersistent(fbUser);

        // register web user with same phone number
        long id = userService.registerWebUser(NAME_A, EMAIL_A, PHONE_A);

        // should be different users
        assertTrue(id != fbUser.getId());
    }

    @Test
    @Ignore("broken - how to do mocking?")
    public void testRegistrationFbUser() throws FbUserLimitException, FbLoginException {
        final String phone = "1234567890";
        ReflectionTestUtils.setField(userService, "fbLoginService", new MockFbLoginService());
        long id = userService.registerFbUser("token", phone);
        User u = roUserDao.findById(id);
        assertEquals(MockFbLoginService.MOCKED_ID, u.getFacebookId());
        assertEquals(phone, u.getManualNumber());
    }

    @Test
    @Ignore("broken - how to do mocking?")
    public void testReRegistrationFbUser() throws FbUserLimitException, FbLoginException {
        ReflectionTestUtils.setField(userService, "fbLoginService", new MockFbLoginService());
        long idA = userService.registerFbUser("token", null);
        long idB = userService.registerFbUser("token", null);
        assertEquals(idA, idB);
    }

    @Test(expected = FbUserLimitException.class)
    @Ignore("broken - how to do mocking?")
    public void testTooFewFriends() throws FbUserLimitException, FbLoginException {
        ReflectionTestUtils.setField(userService, "fbLoginService", new MockFbLoginService(1));
        userService.registerFbUser("token", null);
    }

    private static class MockFbLoginService implements FbLoginService {

        public static final Long MOCKED_ID = 7L;
        public static final String MOCKED_FIRST_NAME = "MockedFirstName";
        public static final String MOCKED_LAST_NAME = "MockedLastName";
        public static final String MOCKED_EMAIL = "mocked@gmail.com";

        private int friendsCount;

        MockFbLoginService() {
            this(100);
        }

        MockFbLoginService(int friendsCount) {
            this.friendsCount = friendsCount;
        }

        @Override
        public UserType getUserInfo(String accessToken) throws FbLoginException {
            UserType u = new UserType();
            u.setId(MOCKED_ID);
            u.setFirstName(MOCKED_FIRST_NAME);
            u.setLastName(MOCKED_LAST_NAME);
            u.setEmail(MOCKED_EMAIL);
            return u;
        }

        @Override
        public Collection<Long> getFriends(String accessToken) throws FbLoginException {
            ArrayList<Long> list = new ArrayList<Long>(friendsCount);
            for (int i = 0; i < friendsCount; i++) {
                list.add((long) i);
            }
            return list;
        }
    }
}
