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

// Get a new quiz question
export const getQuestion = async () => {
  return await apiFetch('/quiz/question');
};

// Submit an answer for validation
export const submitAnswer = async (answerData) => {
  return await apiFetch('/quiz/answer', {
    method: 'POST',
    body: JSON.stringify(answerData),
  });
};

// Submit a player's final score
export const submitScore = async (scoreData) => {
  return await apiFetch('/scores', {
    method: 'POST',
    body: JSON.stringify(scoreData),
  });
};

// Get top scores from the leaderboard
export const getTopScores = async (limit = 10) => {
  return await apiFetch(`/scores/top/${limit}`);
};