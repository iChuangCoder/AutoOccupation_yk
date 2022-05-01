package com.example.autooccupation.bean;

import lombok.Data;

import java.util.HashSet;

@Data
public class JksbBean {

    private String userId;
    private String token;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JksbBean jksbBean = (JksbBean) o;

        if (userId != null ? !userId.equals(jksbBean.userId) : jksbBean.userId != null) return false;
        return token != null ? token.equals(jksbBean.token) : jksbBean.token == null;
    }

    @Override
    public int hashCode() {
        return 231232;
    }

    public static void main(String[] args) {
        JksbBean jksbBean = new JksbBean();
        JksbBean jksbBean1 = new JksbBean();
        jksbBean.setToken("asda");
        jksbBean1.setToken("asda");
        jksbBean.setUserId("qwe");
        jksbBean1.setUserId("qwe");
        HashSet<JksbBean> tasks = new HashSet<>();
        System.out.println(tasks.add(jksbBean));
        System.out.println(tasks.add(jksbBean1));
    }
}
