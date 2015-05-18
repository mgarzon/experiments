package org.jbrt.client.config;

/**
 *
 * @author Cipov Peter
 */
class JMappingBean {

    public static final int DIRECT = 1;
    public static final int PREFIX = 2;

    private String name;
    private JConfigAtom atom;
    private int type;

    public JMappingBean(String name, JConfigAtom atom, int type) {
        this.name = name;
        this.atom = atom;
        this.type = type;
    }

    public JConfigAtom getAtom() {
        return atom;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public void setAtom(JConfigAtom atom) {
        this.atom = atom;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }
    public boolean isPrefixType() {
        return (type == PREFIX);
    }
    public boolean isDirectType() {
        return (type == DIRECT);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JMappingBean other = (JMappingBean) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.atom != other.atom && (this.atom == null || !this.atom.equals(other.atom))) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.atom != null ? this.atom.hashCode() : 0);
        hash = 97 * hash + this.type;
        return hash;
    }



    
}
