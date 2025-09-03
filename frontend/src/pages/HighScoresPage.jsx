import {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Badge} from '@/components/ui/badge';
import {ArrowLeft, Award, Crown, Loader2, Medal, Trophy} from 'lucide-react';
import {toast} from '@/hooks/use-toast';
import {getTopScores} from '@/services/api';

const HighScoresPage = () => {
  const [scores, setScores] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadHighScores();
  }, []);

  const loadHighScores = async () => {
    try {
      setLoading(true);
      const topScores = await getTopScores(10); // Get top 10 scores
      setScores(topScores);
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to load high scores. Please try again.",
        variant: "destructive"
      });
    } finally {
      setLoading(false);
    }
  };

  const handleBackToHome = () => {
    navigate('/');
  };

  const getRankIcon = (rank) => {
    switch (rank) {
      case 1:
        return <Crown className="h-6 w-6 text-yellow-400"/>;
      case 2:
        return <Medal className="h-6 w-6 text-gray-400"/>;
      case 3:
        return <Award className="h-6 w-6 text-orange-400"/>;
      default:
        return <Trophy className="h-5 w-5 text-muted-foreground"/>;
    }
  };

  const getRankStyle = (rank) => {
    switch (rank) {
      case 1:
        return "bg-gradient-to-r from-yellow-500/20 to-yellow-600/20 border-yellow-500/30";
      case 2:
        return "bg-gradient-to-r from-gray-400/20 to-gray-500/20 border-gray-400/30";
      case 3:
        return "bg-gradient-to-r from-orange-400/20 to-orange-500/20 border-orange-400/30";
      default:
        return "";
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  };

  return (
    <div className="quiz-container">
      <div className="max-w-2xl w-full animate-fade-in">
        <Card className="glass-card cinema-glow">
          <CardHeader className="text-center space-y-4">
            <div className="flex justify-center">
              <Trophy className="h-16 w-16 text-primary animate-bounce-in"/>
            </div>
            <CardTitle className="text-3xl font-bold bg-gradient-cinema bg-clip-text text-transparent">
              High Scores
            </CardTitle>
            <p className="text-muted-foreground">
              Hall of Fame - Top Movie Quiz Champions
            </p>
          </CardHeader>

          <CardContent className="space-y-6">
            {loading ? (
              <div className="text-center py-8">
                <Loader2 className="h-12 w-12 animate-spin text-primary mx-auto mb-4"/>
                <p className="text-lg text-muted-foreground">Loading high scores...</p>
              </div>
            ) : scores.length === 0 ? (
              <div className="text-center py-8">
                <Trophy className="h-12 w-12 text-muted-foreground mx-auto mb-4"/>
                <p className="text-lg text-muted-foreground">No scores yet!</p>
                <p className="text-sm text-muted-foreground">Be the first to play and set a record.</p>
              </div>
            ) : (
              <div className="space-y-3">
                {scores.map((score, index) => {
                  const rank = index + 1;
                  return (
                    <div
                      key={score.id}
                      className={`flex items-center justify-between p-4 rounded-lg border transition-all duration-200 hover:scale-[1.02] ${getRankStyle(rank)}`}
                    >
                      <div className="flex items-center space-x-4">
                        <div className="flex items-center justify-center w-10 h-10">
                          {getRankIcon(rank)}
                        </div>
                        <div>
                          <p className="font-semibold text-lg">{score.username}</p>
                          <p className="text-sm text-muted-foreground">
                            {formatDate(score.createTime)}
                          </p>
                        </div>
                      </div>

                      <div className="text-right">
                        <Badge variant="secondary" className="text-lg px-3 py-1">
                          {score.score}
                        </Badge>
                        <p className="text-xs text-muted-foreground mt-1">
                          #{rank}
                        </p>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}

            <div className="pt-4">
              <Button
                onClick={handleBackToHome}
                variant="outline"
                className="w-full h-12 text-lg"
                size="lg"
              >
                <ArrowLeft className="mr-2 h-5 w-5"/>
                Back to Home
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default HighScoresPage;