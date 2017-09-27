package com.mcndsj.YourCraftCore.queueSystem.Queues;

import com.mcndsj.YourCraftCore.queueSystem.ServerInfo;

import java.util.*;

/**
 * Created by Matthew on 2016/4/24.
 */
public class ServerListQueue implements List<ServerInfo>{
    LinkedList<ServerInfo> list = new LinkedList<>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<ServerInfo> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(ServerInfo serverInfo) {
        //TODO: implements here
        if (serverInfo.getOnline() == 0 || serverInfo.getOnline() == serverInfo.getMax()) {
            return list.add(serverInfo);
        }

        int counter = 0;
        for (ServerInfo info : list) {
            if (info.getOnline() < serverInfo.getOnline()) {
                break;
            }
            counter++;
        }
        list.add(counter, serverInfo);
        return true;

    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);

    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ServerInfo> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends ServerInfo> c) {
        return list.addAll(index,c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public ServerInfo get(int index) {
        return list.get(index);
    }

    @Override
    public ServerInfo set(int index, ServerInfo element) {
        return list.set(index,element);
    }

    @Override
    public void add(int index, ServerInfo element) {
        list.add(index,element);
    }

    @Override
    public ServerInfo remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<ServerInfo> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<ServerInfo> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<ServerInfo> subList(int fromIndex, int toIndex) {
        return subList(fromIndex,toIndex);
    }
}
