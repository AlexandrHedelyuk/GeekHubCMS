package org.geekhub.hibernate.dao.impl;

import org.geekhub.hibernate.dao.UserDao;
import org.geekhub.hibernate.entity.User;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public class UserDaoImpl extends GenericDaoImpl<User> implements UserDao {


    @Override
    public User getUserById(int userId) {

        User user = (User) sessionFactory.getCurrentSession().get(User.class, userId);


        return user;
    }
    @Transactional
    public void addUser(User user) {

        sessionFactory.getCurrentSession().save(user);


    }

    @Override
    public User loadUserByUsername(String userName) throws UsernameNotFoundException {

        List<User> list = sessionFactory.getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.eq("login", userName)).list();

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }


    @Override
    public User getUserByEmail(String email) throws UsernameNotFoundException {

        User user = (User)sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("email", email)).uniqueResult();


        return user;
    }

    @Override
    public User getUserByLogin(String login) throws UsernameNotFoundException {

        User user = (User)sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("login", login)).uniqueResult();


        return user;
    }
    @Override
    public List<User> usersOnPage(int page) throws UsernameNotFoundException {
//        return sessionFactory.getCurrentSession().createQuery("from User user ORDER BY user.LAST_NAME desc").setFirstResult(3).setMaxResults(3).list();
        return sessionFactory.getCurrentSession().createCriteria(User.class).setFirstResult(3 * (page - 1)).setMaxResults(3).list();
    }
}
