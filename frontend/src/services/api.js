// API service for Movie Quiz backend communication

const API_BASE_URL = 'http://localhost:8080/api';

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

// Start a new game
export const startNewGame = async (username) => {
  return await apiFetch(`/quiz/start?username=${encodeURIComponent(username)}`, {
    method: 'POST',
  });
};

// Submit an answer for validation
export const submitAnswer = async (answerData) => {
  return await apiFetch('/quiz/answer', {
    method: 'POST',
    body: JSON.stringify(answerData),
  });
};

// Get top scores from the leaderboard
export const getTopScores = async (limit = 10) => {
  return await apiFetch(`/sessions/top/${limit}`);
};
