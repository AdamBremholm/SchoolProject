package se.alten.schoolproject.dao;

import javax.ejb.Remote;

@Remote
public interface SchoolAccessRemote<T, V> extends SchoolAccessLocal<T, V> {
}
