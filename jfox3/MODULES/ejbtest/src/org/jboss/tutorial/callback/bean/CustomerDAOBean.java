/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.tutorial.callback.bean;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.interceptor.Interceptors;

@Stateless
@Remote(CustomerDAO.class)
@Interceptors({LifecycleInterceptor.class})
public class CustomerDAOBean implements CustomerDAO {
    @PersistenceContext
    private EntityManager manager;

    public int create(String first, String last, String street, String city, String state, String zip) {
        System.out.println("-- CustomerDAOBean.create()");
        Customer customer = new Customer(first, last, street, city, state, zip);
        manager.persist(customer);
        return customer.getId();
    }

    public Customer find(int id) {
        System.out.println("-- CustomerDAOBean.find()");
        return manager.find(Customer.class, id);
    }

    public List findByLastName(String last) {
        System.out.println("-- CustomerDAOBean.findByLastName(id)");
        return manager.createQuery("from Customer c where c.last = :last").setParameter("last", last).getResultList();
    }

    public void merge(Customer c) {
        System.out.println("-- CustomerDAOBean.merge()");
        manager.merge(c);
    }

    public void delete(List l) {
        System.out.println("-- CustomerDAOBean.delete()");
        for (Iterator it = l.iterator(); it.hasNext();) {
            Customer c = (Customer)it.next();
            manager.remove(c);
        }
    }

    // Callbacks ----------------------------------------------------------------
    @PostConstruct
    public void postConstructCallback() {
        System.out.println("PostConstruct - Have EntityManager: " + (manager != null));
    }

    @PreDestroy
    public void preDestroyCallback() {
        System.out.println("PreDestory - Have EntityManager: " + (manager != null));
    }
}
