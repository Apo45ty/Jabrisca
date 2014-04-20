package edu.uprm.ece.icom4015.jabrisca.client;

// / This model operates in two ways one is a state machine and below are
// its states
enum ModelStates {
	loginsingup() {
		@Override
		void setStateParametersValues() {
			String[] stateParameters = { "username", "password" };
			parameterKeys = stateParameters;
		}
	},
	lobby() {
		@Override
		void setStateParametersValues() {
			String[] stateParameters = { "username", "password" };
			parameterKeys = stateParameters;
		}
	},
	newgame() {
		@Override
		void setStateParametersValues() {
			String[] stateParameters = { "username", "password", "roomName",
					"teamGame", "blackhand", "cardSwap", "surrender",
					"timeLimit", "tournament" };
			parameterKeys = stateParameters;
		}
	},
	gameboard() {
		@Override
		void setStateParametersValues() {
			String[] stateParameters = { "username", "password", "roomName",
					"teamGame", "blackhand", "cardswap", "surrender",
					"timeLimit", "tournament", "playCardAnimationName",
					"playCardAnimationParameters", "playerSeat",
					"boardGame_myCard1", "boardGame_myCard2",
					"boardGame_myCard3", "boardGame_player1Card",
					"boardGame_player2Card", "boardGame_player3Card",
					"boardGame_player4Card"
			// number given to the player by the server to that is the
			// players original position in the first play or his "seat" in
			// the game
			};
			parameterKeys = stateParameters;
		}
	},
	endgame() {
		@Override
		void setStateParametersValues() {
			String[] stateParameters = { "username", "password", "roomName",
					"teamGame", "blackhand", "cardSwap", "surrender",
					"timeLimit", "tournament" };
			parameterKeys = stateParameters;
		}
	};

	Object[] parameterValues = {

	};
	String[] parameterKeys;

	/**
	 * Constructor
	 */
	private ModelStates() {
		setStateParametersValues();
	}

	/**
	 * Conver the state number and state parameter along with the state values
	 * to string
	 */
	public String toString() {
		String parameters = ":";
		parameters += getStateParametersValueKeyPair();
		int stateNumber = Integer.parseInt(this.getClass().getTypeName()
				.split("\\$ModelStates\\$")[1]);
		return stateNumber + parameters;
	}

	/**
	 * Manually set all the values for the parameterValues
	 * 
	 * @param parameterValues
	 */
	public void setStateParameterValues(Object[] parameterValues) {
		this.parameterValues = parameterValues;
	}

	/**
	 * This method is ran when each instance of the enum is instantiated- its
	 * purpose is to allow each state to specify its state parameters
	 */
	abstract void setStateParametersValues();

	/**
	 * @return the parameterKeys
	 */
	public synchronized String[] getParameterKeys() {
		return parameterKeys;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public Object getStateParameterValue(int index) {
		return parameterValues[index];
	}

	/**
	 * @param index
	 *            an integer that represents a position
	 * @return specific key associated with the index
	 */
	public Object getStateParameterKey(int index) {
		return parameterKeys[index];
	}

	/**
	 * get a specific parameter
	 * 
	 * @param key
	 * @return
	 */
	public Object getStateParameterValue(String key) {
		int index = -1;
		for (int i = 0; i < parameterKeys.length; i++) {
			if (key.equals(parameterKeys[i])) {
				index = i;
				break; // exit loop
			}
		}

		// Check if card could be found
		if (index < 0) {
			return null;
		} // else return the found card if so
		return parameterValues[index];
	}

	/**
	 * get a specific parameter
	 * 
	 * @param key
	 * @return
	 */
	public Object setStateParameterValue(String key, Object value) {
		int index = -1;
		for (int i = 0; i < parameterKeys.length; i++) {
			if (key.equals(parameterKeys[i])) {
				index = i;
				break; // exit loop
			}
		}

		// Check if card could be found
		if (index < 0) {
			return null;
		} // else return the found card if so
		if (parameterValues.length < parameterKeys.length) {
			Object[] temp = new Object[parameterKeys.length];
			for (int i = 0; i < parameterValues.length; i++) {
				temp[i] = parameterValues[i];
			}
			parameterValues = temp;
		}
		Object result = parameterValues[index];
		parameterValues[index] = value;
		return result;
	}

	/**
	 * return all the values for each parameter
	 * 
	 * @return
	 */
	public Object[] getStateParametersValues() {
		return parameterValues;
	}

	public String getStateParametersValueKeyPair() {
		String parameters = "";
		for (int i = 0; i < parameterKeys.length; i++) {
			// Add the parameter key
			parameters += parameterKeys[i] + "=";
			// add the parameter values if you can null otherwise
			if (parameterValues.length > i) {
				parameters += parameterValues[i];
			} else {
				parameters += "null";
			}
			// add trailing comma to all but the last element
			if (i < parameterKeys.length - 1)
				parameters += ",";
		}
		return parameters;
	}
	// \"static ModelStates values()\" returns all values of the enum
}