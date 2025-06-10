package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiscodeitApplicationTests {

	@Test
	void contextLoads() {
	}

	private static EntityManagerFactory emf;
	private EntityManager em;

	@BeforeAll
	public static void initFactory() {
		emf = Persistence.createEntityManagerFactory("discodeit test");
	}

	@BeforeEach
	public void initManager() { em = emf.createEntityManager();}

	@AfterAll
	public static void closeFactory() { emf.close();}

	@AfterEach
	public void closeEntityManager() { em.close();}


	@Test
	public void User_UserStatus_OneToOne_Test() {

		// given
		String name = "홍길동";

		// when
		User user1 = em.find(User.class, name);
		UserStatus userStatus1 = user1.getStatus();

		// then
		assertNotNull(userStatus1);
		System.out.println("user1 = " + user1);
		System.out.println("userStatus1 = " + userStatus1);
	}
}
