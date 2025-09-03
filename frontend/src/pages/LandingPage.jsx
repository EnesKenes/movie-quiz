import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {Button} from '@/components/ui/button';
import {Input} from '@/components/ui/input';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Film, Play, Trophy} from 'lucide-react';
import {toast} from '@/hooks/use-toast';

const LandingPage = () => {
  const [username, setUsername] = useState('');
  const navigate = useNavigate();

  const handleStartQuiz = () => {
    if (!username.trim()) {
      toast({
        title: "Username Required",
        description: "Please enter a username to start the quiz.",
        variant: "destructive"
      });
      return;
    }

    if (username.length > 20) {
      toast({
        title: "Username Too Long",
        description: "Username must be 20 characters or less.",
        variant: "destructive"
      });
      return;
    }

    // Store username in sessionStorage for use during the game
    sessionStorage.setItem('movieQuizUsername', username);
    navigate('/quiz');
  };

  const handleViewHighScores = () => {
    navigate('/high-scores');
  };

  return (
    <div className="quiz-container">
      <div className="max-w-md w-full animate-fade-in">
        <Card className="glass-card cinema-glow">
          <CardHeader className="text-center space-y-4">
            <div className="flex justify-center">
              <Film className="h-16 w-16 text-primary animate-bounce-in"/>
            </div>
            <CardTitle className="text-4xl font-bold bg-gradient-cinema bg-clip-text text-transparent">
              Movie Quiz
            </CardTitle>
            <p className="text-muted-foreground text-lg">
              Test your knowledge of cinema!
            </p>
          </CardHeader>

          <CardContent className="space-y-6">
            <div className="space-y-3">
              <label htmlFor="username" className="text-sm font-medium text-foreground">
                Enter your username
              </label>
              <Input
                id="username"
                type="text"
                placeholder="Your username..."
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                maxLength={20}
                className="text-center text-lg"
                onKeyPress={(e) => e.key === 'Enter' && handleStartQuiz()}
              />
              <p className="text-xs text-muted-foreground text-center">
                {username.length}/20 characters
              </p>
            </div>

            <div className="space-y-3">
              <Button
                onClick={handleStartQuiz}
                className="w-full h-12 text-lg font-semibold"
                size="lg"
              >
                <Play className="mr-2 h-5 w-5"/>
                Start Quiz
              </Button>

              <Button
                onClick={handleViewHighScores}
                variant="outline"
                className="w-full h-12 text-lg"
                size="lg"
              >
                <Trophy className="mr-2 h-5 w-5"/>
                View High Scores
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default LandingPage;