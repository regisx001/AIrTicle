// API Configuration
export const API_CONFIG = {
	BASE_URL: 'http://localhost:8080/api',
	TIMEOUT: 30000,
	RETRY_ATTEMPTS: 3
};

// Environment-specific configuration
export const getApiBaseUrl = () => {
	if (typeof window !== 'undefined') {
		// Client-side
		return API_CONFIG.BASE_URL;
	}
	// Server-side - use import.meta.env for Vite
	return import.meta.env.VITE_API_BASE_URL || API_CONFIG.BASE_URL;
};
