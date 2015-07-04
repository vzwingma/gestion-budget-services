/**
 * 
 */
package com.terrier.finances.gestion.data.mongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Configuration de connexion à la BDD
 * @author vzwingma
 *
 */
@Service
public class SpringMongoDBConfig {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringMongoDBConfig.class);
	
	private String ipServer;
	private String portServer;
	private String loginMongoDB;
	private String pwdMongoDB;
	private String nomMongoDB;
	private static final int DEFAULT_PORT_MONGODB = 27017;

	


	/**
	 * @return the ipServer
	 */
	public String getIpServer() {
		return ipServer;
	}

	/**
	 * @param ipServer the ipServer to set
	 */
	@Value("${budget.data.ip}")
	public void setIpServer(String ipServer) {
		this.ipServer = ipServer;
	}

	/**
	 * @return the portServer
	 */
	public int getPortServer() {
		try{
			return Integer.parseInt(this.portServer);
		}
		catch(NumberFormatException e){
			LOGGER.warn("Le port configuré [{}] est incorrect. Utilisation de la valeur par défaut : [{}]", this.portServer, DEFAULT_PORT_MONGODB);
		}
		return DEFAULT_PORT_MONGODB;
	}

	/**
	 * @param portServer the portServer to set
	 */
	@Value("${budget.data.port}")
	public void setPortServer(String portServer) {
		this.portServer = portServer;
	}

	/**
	 * @return the loginMongoDB
	 */
	public String getLoginMongoDB() {
		return loginMongoDB;
	}

	/**
	 * @param loginMongoDB the loginMongoDB to set
	 */
	@Value("${budget.data.login}")
	public void setLoginMongoDB(String loginMongoDB) {
		this.loginMongoDB = loginMongoDB;
	}

	/**
	 * @return the pwdMongoDB
	 */
	public String getPwdMongoDB() {
		return pwdMongoDB;
	}

	/**
	 * @param pwdMongoDB the pwdMongoDB to set
	 */
	@Value("${budget.data.password}")
	public void setPwdMongoDB(String pwdMongoDB) {
		this.pwdMongoDB = pwdMongoDB;
	}

	/**
	 * @return the nomMongoDB
	 */
	public String getNomMongoDB() {
		return nomMongoDB;
	}

	/**
	 * @param nomMongoDB the nomMongoDB to set
	 */
	@Value("${budget.data.nom}")
	public void setNomMongoDB(String nomMongoDB) {
		this.nomMongoDB = nomMongoDB;
	}
}
