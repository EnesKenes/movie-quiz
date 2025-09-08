// API service for Movie Quiz backend communication

const API_BASE_URL = 'http://192.168.1.24:8080/api';
const USE_GEMINI = import.meta.env.VITE_USE_GEMINI === 'true';

// Generic fetch wrapper with error handling
const apiFetch = async (url, options = {}) => {
  try {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });

    if (!response.ok) {
      throw new Error(`API Error: ${response.status} ${response.statusText}`);
    }

    return await response.json();
  } catch (error) {
    console.error('API call failed:', error);
    throw error;
  }
};

// Fetch question based on USE_GEMINI

// Start a new game
export const startNewGame = async (username) => {
  const path = USE_GEMINI ? '/gemini-quiz/start' : '/quiz/start';
  return await apiFetch(`${path}?username=${encodeURIComponent(username)}`, {
    method: 'POST',
  });
};

// Submit an answer
export const submitAnswer = async (answerData) => {
  const path = USE_GEMINI ? '/gemini-quiz/answer' : '/quiz/answer';
  return await apiFetch(path, {
    method: 'POST',
    body: JSON.stringify(answerData),
  });
};

// Get top scores from the leaderboard
export const getTopScores = async (limit = 10) => {
  return await apiFetch(`/sessions/top/${limit}`);
};
