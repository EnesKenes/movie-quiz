import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Trophy, Play, Loader2, Star } from 'lucide-react';
import { toast } from '@/hooks/use-toast';
import { submitScore } from '@/services/api';

const GameOverScreen = () => {
  const [submitting, setSubmitting] = useState(false);
  const [scoreSubmitted, setScoreSubmitted] = useState(false);
  const navigate = useNavigate();
  
  const username = sessionStorage.getItem('movieQuizUsername');
  const finalScore = parseInt(sessionStorage.getItem('movieQuizFinalScore') || '0');

  useEffect(() => {
    if (!username) {
      navigate('/');
      return;
    }
    
    // Auto-submit score when component mounts
    handleSubmitScore();
  }, [username, navigate]);

  const handleSubmitScore = async () => {
    if (scoreSubmitted || submitting) return;
    
    setSubmitting(true);
    
    try {
      await submitScore({
        username: username,
        score: finalScore
      });
      
      setScoreSubmitted(true);
      toast({
        title: "Score Saved! 🎉",
        description: "Your score has been added to the leaderboard.",
      });
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to save score. You can still view the leaderboard.",
        variant: "destructive"
      });
    } finally {
      setSubmitting(false);
    }
  };

  const handlePlayAgain = () => {
    // Clear the final score but keep the username
    sessionStorage.removeItem('movieQuizFinalScore');
    navigate('/quiz');
  };

  const handleViewHighScores = () => {
    navigate('/high-scores');
  };

  const handleBackToHome = () => {
    // Clear all session data
    sessionStorage.removeItem('movieQuizUsername');
    sessionStorage.removeItem('movieQuizFinalScore');
    navigate('/');
  };

  const getScoreMessage = (score) => {
    if (score === 0) return "Better luck next time!";
    if (score <= 3) return "Not bad for a start!";
    if (score <= 7) return "Good knowledge!";
    if (score <= 12) return "Impressive!";
    if (score <= 18) return "Outstanding!";
    return "Movie genius! 🌟";
  };

  const getScoreColor = (score) => {
    if (score === 0) return "text-muted-foreground";
    if (score <= 3) return "text-orange-400";
    if (score <= 7) return "text-yellow-400";
    if (score <= 12) return "text-blue-400";
    if (score <= 18) return "text-purple-400";
    return "text-yellow-300";
  };

  return (
    <div className="quiz-container">
      <div className="max-w-md w-full animate-fade-in">
        <Card className="glass-card cinema-glow">
          <CardHeader className="text-center space-y-4">
            <div className="flex justify-center">
              <Trophy className={`h-20 w-20 ${getScoreColor(finalScore)} animate-bounce-in`} />
            </div>
            <CardTitle className="text-3xl font-bold">
              Game Over!
            </CardTitle>
            <p className="text-muted-foreground">
              Thanks for playing, <span className="text-foreground font-semibold">{username}</span>!
            </p>
          </CardHeader>
          
          <CardContent className="space-y-6">
            {/* Final Score Display */}
            <div className="text-center space-y-3">
              <Badge variant="secondary" className="text-2xl px-6 py-3">
                <Star className="mr-2 h-6 w-6" />
                Final Score: {finalScore}
              </Badge>
              <p className={`text-lg font-medium ${getScoreColor(finalScore)}`}>
                {getScoreMessage(finalScore)}
              </p>
            </div>

            {/* Score Submission Status */}
            {submitting && (
              <div className="text-center space-y-2">
                <Loader2 className="h-6 w-6 animate-spin text-primary mx-auto" />
                <p className="text-sm text-muted-foreground">Saving your score...</p>
              </div>
            )}
            
            {scoreSubmitted && (
              <div className="text-center">
                <p className="text-sm text-green-400">✓ Score saved to leaderboard!</p>
              </div>
            )}
            
            {/* Action Buttons */}
            <div className="space-y-3">
              <Button 
                onClick={handlePlayAgain}
                className="w-full h-12 text-lg font-semibold"
                size="lg"
              >
                <Play className="mr-2 h-5 w-5" />
                Play Again
              </Button>
              
              <Button 
                onClick={handleViewHighScores}
                variant="outline"
                className="w-full h-12 text-lg"
                size="lg"
              >
                <Trophy className="mr-2 h-5 w-5" />
                View High Scores
              </Button>
              
              <Button 
                onClick={handleBackToHome}
                variant="ghost"
                className="w-full h-10 text-base"
              >
                Back to Home
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default GameOverScreen;
