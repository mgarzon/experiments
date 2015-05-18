package org.jbrt.client.config;


public class JConfigAtom implements java.io.Serializable {

    private static final long serialVersionUID = 12345001L;

    private final String id;
	private final String url;
	private final String project;
	private final String version;
	private final String language;
	private final int hashCode;

    public JConfigAtom(String url, String project, String version, String language) {
        this.id = null;
		this.language = language;
		this.project = project;
		this.url = url;
		this.version = version;
		this.hashCode = countHashCode();
	}
	
	public JConfigAtom(String id, String url, String project, String version, String language) {
        this.id = id;
		this.language = language;
		this.project = project;
		this.url = url;
		this.version = version;
		this.hashCode = countHashCode();
	}

    public String getID() {
        return this.id;
    }

	public String getUrl() {
		return url;
	}

	public String getProject() {
		return project;
	}

	public String getVersion() {
		return version;
	}

	public String getLanguage() {
		return language;
	}



	
	private int countHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}
	
	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JConfigAtom other = (JConfigAtom) obj;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URL: " + this.url + 
				"; PROJECT: " + this.project +
				"; VERSION:" + this.version +
				"; LANGUAGE:" + this.language +"\n";
	}
	
}
