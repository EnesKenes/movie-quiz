import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Loader2, Star, Timer } from 'lucide-react';
import { toast } from '@/hooks/use-toast';
import { getQuestion, submitAnswer } from '@/services/api';

const QuizScreen = () => {
  const [question, setQuestion] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [score, setScore] = useState(0);
  const navigate = useNavigate();

  const username = sessionStorage.getItem('movieQuizUsername');

  useEffect(() => {
    if (!username) {
      navigate('/');
      return;
    }

    // Load persisted question if exists
    const savedQuestion = sessionStorage.getItem('currentQuestion');
    if (savedQuestion) {
      setQuestion(JSON.parse(savedQuestion));
      setLoading(false);
    } else {
      loadQuestion();
    }
  }, [username, navigate]);

  const loadQuestion = async () => {
    try {
      setLoading(true);
      const questionData = await getQuestion();
      setQuestion(questionData);
      setSelectedAnswer(null);

      // Persist current question
      sessionStorage.setItem('currentQuestion', JSON.stringify(questionData));
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to load question. Please try again.",
        variant: "destructive"
      });
    } finally {
      setLoading(false);
    }
  };

  const handleAnswerSelect = async (answer) => {
    if (submitting) return;

    setSelectedAnswer(answer);
    setSubmitting(true);

    try {
      const result = await submitAnswer({
        selectedAnswer: answer,
        token: question.token
      });

      if (result.correct) {
        const newScore = score + 1;
        setScore(newScore);

        toast({
          title: "Correct! 🎉",
          description: "Great job! Loading next question...",
        });

        setTimeout(() => {
          if (result.nextQuestion) {
            setQuestion(result.nextQuestion);
            sessionStorage.setItem('currentQuestion', JSON.stringify(result.nextQuestion));
            setSelectedAnswer(null);
            setSubmitting(false);
          } else {
            loadQuestion();
            setSubmitting(false);
          }
        }, 1500);
      } else {
        // Wrong answer - game over
        sessionStorage.setItem('movieQuizFinalScore', score.toString());
        sessionStorage.removeItem('currentQuestion'); // remove persisted question
        toast({
          title: "Incorrect! ❌",
          description: "Game over! Redirecting to results...",
          variant: "destructive"
        });

        setTimeout(() => {
          navigate('/game-over');
        }, 2000);
      }
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to submit answer. Please try again.",
        variant: "destructive"
      });
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="quiz-container">
        <Card className="glass-card max-w-2xl w-full">
          <CardContent className="flex items-center justify-center py-16">
            <div className="text-center space-y-4">
              <Loader2 className="h-12 w-12 animate-spin text-primary mx-auto" />
              <p className="text-lg text-muted-foreground">Loading question...</p>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (!question) {
    return (
      <div className="quiz-container">
        <Card className="glass-card max-w-2xl w-full">
          <CardContent className="text-center py-16">
            <p className="text-lg text-muted-foreground">Failed to load question</p>
            <Button onClick={loadQuestion} className="mt-4">
              Try Again
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="quiz-container">
      <div className="max-w-4xl w-full space-y-6 animate-fade-in">
        {/* Score and Type Header */}
        <div className="flex justify-between items-center">
          <Badge variant="secondary" className="text-lg px-4 py-2">
            <Star className="mr-2 h-4 w-4" />
            Score: {score}
          </Badge>
          <Badge variant="outline" className="text-lg px-4 py-2">
            <Timer className="mr-2 h-4 w-4" />
            {question.type}
          </Badge>
        </div>

        {/* Main Question Card */}
        <Card className="glass-card cinema-glow">
          <CardHeader className="text-center">
            {question.imageUrl && (
              <div className="flex justify-center mb-4">
                <img
                  src={question.imageUrl}
                  alt="Movie poster"
                  className="rounded-lg shadow-lg max-h-64 object-cover"
                  onError={(e) => {
                    e.target.style.display = 'none';
                  }}
                />
              </div>
            )}
            <CardTitle className="text-2xl font-bold leading-relaxed">
              {question.questionText}
            </CardTitle>
          </CardHeader>

          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {question.options.map((option, index) => (
                <Button
                  key={index}
                  onClick={() => handleAnswerSelect(option)}
                  disabled={submitting}
                  variant={selectedAnswer === option ? "default" : "outline"}
                  className="h-16 text-lg font-medium transition-all duration-200 hover:scale-105"
                  size="lg"
                >
                  {submitting && selectedAnswer === option ? (
                    <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                  ) : null}
                  {option}
                </Button>
              ))}
            </div>

            {submitting && (
              <div className="text-center mt-6">
                <p className="text-muted-foreground">Checking your answer...</p>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default QuizScreen;
