package org.sensation.snapmemo.server.Data;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.sensation.snapmemo.server.PO.GroupPO;
import org.sensation.snapmemo.server.PO.MemoPO;
import org.sensation.snapmemo.server.PO.UserPO;

public class MySessionFactory {
	static SessionFactory sessionFactory;
	public static void main(String[] args){
		MySessionFactory my = new MySessionFactory();
		try {
			my.setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("setup over!");
		
//		GroupPO group = new GroupPO("0001","Sensation");
//		UserPO user1 = new UserPO("001","Anthony","123456");
//		UserPO user2 = new UserPO("002","Alan","123456");
//		MemoPO memo1 = new MemoPO("001","topic1","2016-02-17 02:14","content1");
//		MemoPO memo2 = new MemoPO("002","topic2","2016-02-17 02:15","content2");
//		user1.addMemo(memo1);
//		user2.addMemo(memo2);
//		group.addUser(user1);
//		group.addUser(user2);
//		
//		Session session = sessionFactory.openSession();
//		session.beginTransaction();
//		session.save(memo1);
//		session.save(memo2);
//		session.save(user1);
//		session.save(user2);
//		session.save(group);
//		
//		session.getTransaction().commit();
//		session.close();
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery( "from GroupPO" ).list();
		for(GroupPO group : (List<GroupPO>)result){
			Iterator<UserPO> it = group.getUsers();
			while(it.hasNext()){
				Iterator<MemoPO> mit = it.next().getMemos();
				while(mit.hasNext()){
					System.out.println(mit.next().getMemoId());
				}
			}
				
		}
		session.getTransaction().commit();
		session.close();
	}
	protected void setUp() throws Exception {
		// A SessionFactory is set up once for an application!
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
		}
		catch (Exception e) {
			System.out.println("Exception happened!!!");
			// The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
			// so destroy it manually.
			StandardServiceRegistryBuilder.destroy( registry );
		}
	}
}
