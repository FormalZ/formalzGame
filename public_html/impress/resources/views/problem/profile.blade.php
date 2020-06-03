{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.app')
@section('content')
    <div class="container">
        <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        @if($problem->archive)
                            ARCHIVED -
                        @elseif ($problem->hide)
                            HIDDEN -
                        @endif
                        {{$problem->header}}
                        @if($problem->deadline != 0)
                            <div style="float: right">Deadline: {{date("d-m-Y H:i", $problem->deadline)}}</div>
                        @endif
                    </div>

                    <div class="panel-body">
                        @if (session('status'))
                            <div class="alert alert-success">
                                {{ session('status') }}
                            </div>
                        @endif
                        @if ($iscreator)
                            {{$problem->description}}
                        @endif
                        @if ($iscreator && $dashboardUrl != NULL)
                            <br>Tracking enabled: <a
                                    href="{{ $dashboardUrl }}">Dashboard</a>
                        @endif
                        <br/>
                        <br/>
                        <br/>
                        <div class="well">
                            @if ($problem->deadline >= time() || $problem->deadline == 0)
                                <button onclick="window.location.href='/problem/{{$problem->id}}/play'">Play Problem
                                </button>
                            @endif
                            @if ($iscreator)
                                <button onclick="window.location.href='/problem/{{$problem->id}}/edit'">Edit Problem
                                </button>
                            @endif
                            <button onclick="window.location.href='/room/{{$room->url}}'">Back to Room</button>
                            @if ($iscreator)
                                <button type="button" data-toggle="collapse"
                                        data-target="#advancedOptions" aria-expanded="false"
                                        aria-controls="advancedOptions">Advanced options
                                </button>
                                <div id="advancedOptions" class="collapse">
                                    <br>

                                    <button onclick="window.location.href='/problem/{{$problem->id}}/hide'">
                                        @if($problem->hide)
                                            Unhide
                                        @else
                                            Hide
                                        @endif
                                        Problem
                                    </button>
                                    <button onclick="window.location.href='/problem/{{$problem->id}}/archive'">
                                        @if($problem->archive)
                                            Unarchive
                                        @else
                                            Archive
                                        @endif
                                        Problem
                                    </button>
                                    <button onclick="window.location.href='/problem/{{$problem->id}}/refresh'">Refresh
                                        Problem
                                    </button>
                                    <button onclick="window.location.href='/problem/{{$problem->id}}/remakepath'">
                                        Generate New Path
                                    </button>
                                    @if ($problem->trackingCode == NULL && $isAnalyticsEnabled )
                                        <button onclick="window.location.href='/problem/{{$problem->id}}/addtracking'">
                                            Add tracking
                                        </button>
                                    @endif
                                </div>
                            @endif
                            @if ($problem->deadline <= time() && $problem->deadline != 0)
                                <p>Deadline has passed</p>
                            @endif
                        </div>

                        <ul class="nav nav-tabs">
                            <li class="active"><a data-toggle="tab" href="#highscores">Highscores</a></li>
                            <li><a data-toggle="tab" href="#yourscores">Your scores</a></li>
                            <li><a data-toggle="tab" href="#yourlastgame">Your last game</a></li>
                            @if ($iscreator)
                                <li><a data-toggle="tab" href="#statistics">Statistics</a></li>
                                <li><a data-toggle="tab" href="#completed">Student completion</a></li>
                            @endif
                        </ul>
                        {{-- Show a table of highscores, can be filtered to your own highscores only --}}
                        <div class="tab-content">
                            <div id="highscores" class="tab-pane fade in active">
                                <h3>Highscores</h3>
                                <p>
                                <table class="table">
                                    <thead>
                                    <tr>
                                        <th scope="col">#</th>
                                        <th scope="col">Name</th>
                                        <th scope="col">Score</th>
                                        <th scope="col">Date</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    @php
                                        {{$i = 0;}}
                                    @endphp
                                    @foreach($scores as $s)
                                        <tr>
                                            <th scope="row">{{++$i}}</th>
                                            <td>{{$s->name}}</td>
                                            <td>{{$s->score}}</td>
                                            <td>{{$s->created_at}}</td>
                                        </tr>
                                    @endforeach
                                    </tbody>
                                </table>
                                </p>
                            </div>
                            <div id="yourscores" class="tab-pane fade">
                                <h3>Your scores</h3>
                                <p>
                                <table class="table">
                                    <thead>
                                    <tr>
                                        <th scope="col">#</th>
                                        <th scope="col">Name</th>
                                        <th scope="col">Score</th>
                                        <th scope="col">Date</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    @php
                                        $i = 0;
                                    @endphp
                                    @foreach($scores as $s)
                                        @if($s->user_id == Auth::id())

                                            <tr>
                                                <th scope="row">{{++$i}}</th>
                                                <td>{{$s->name}}</td>
                                                <td>{{$s->score}}</td>
                                                <td>{{$s->created_at}}</td>
                                            </tr>
                                        @endif
                                    @endforeach
                                    </tbody>
                                </table>
                                </p>
                            </div>
                            {{-- Show a summary of the last game of the player for feedback reasons --}}
                            <div id="yourlastgame" class="tab-pane fade">
                                <h3>Your last game</h3>
                                <p>
                                    @if (count($statistics['lastgameproblems']) > 0)
                                        Mistakes per problem:
                                <div id="lastgamechart" style="width:700px;height:500px;"></div>
                                <script type="text/javascript">
                                    var LASTGAMECHART = document.getElementById('lastgamechart');
                                    var ydatapre = [];
                                    var xdata = [];
                                    var ydatapost = [];
                                    @for($j = 0; $j < count($statistics['lastgameproblems']); $j++)
                                    ydatapre.push({{$statistics['lastgameproblems'][$j]->pre_mistakes}});
                                    ydatapost.push({{$statistics['lastgameproblems'][$j]->post_mistakes}});
                                    xdata.push('Problem ' + {{$j + 1}} +' (Difficulty ' + {{$statistics['lastgameproblems'][$j]->difficulty}} +')');
                                            @endfor
                                    var tracepre = {
                                            x: xdata,
                                            y: ydatapre,
                                            type: 'bar',
                                            name: 'Preconditions',
                                            marker: {
                                                color: 'rgb(0,0,255)',
                                                opacity: 1
                                            }
                                        };

                                    var tracepost = {
                                        x: xdata,
                                        y: ydatapost,
                                        type: 'bar',
                                        name: 'Postconditions',
                                        marker: {
                                            color: 'rgb(255,0,0)',
                                            opacity: 1
                                        }
                                    };

                                    var layout = {
                                        xaxis: {
                                            title: 'problems'
                                        },
                                        yaxis: {
                                            title: 'amount of mistakes'
                                        },
                                        barmode: 'group'
                                    };
                                    Plotly.plot(LASTGAMECHART, [tracepre, tracepost], layout, {staticPlot: true});
                                </script>
                                @else
                                    You have not played this problem yet.
                                    @endif
                                    </p>
                            </div>
                            {{-- Show the statistics for the teacher: --}}
                            @if ($iscreator)
                                <div id="statistics" class="tab-pane fade">
                                    <h3>Statistics</h3>
                                    <p>
                                    <ul class="list-group">
                                        <li class="list-group-item">
                                            <span class="badge">{{ $statistics['completecount'] }}</span>
                                            Amount of successful problem solving attempts:
                                        </li>
                                        <li class="list-group-item">
                                            <span class="badge">{{ $statistics['averagescore'] }}</span>
                                            Average score:
                                        </li>
                                        <li class="list-group-item">
                                            <span class="badge">{{ $statistics['averagetime'] }}</span>
                                            Average problem completion time:
                                        </li>
                                        {{--Graph cumulative charts using plotly.js --}}
                                        <li class="list-group-item">
                                            Cumulative score graph:
                                            <div id="scorechart" style="width:700px;height:500px;"></div>
                                            <script type="text/javascript">
                                                var SCORECHART = document.getElementById('scorechart');
                                                var ydata = [1];
                                                var xdata = [0];
                                                @for($j = $scores->count() - 1; $j >= 0; $j--)
                                                ydata.push({{($j + 1) / $scores->count()}});
                                                xdata.push({{$scores[$j]->score}});
                                                        @endfor
                                                var data = {
                                                        x: xdata,
                                                        y: ydata,
                                                        type: 'scatter',
                                                        fill: 'tozeroy'
                                                    };
                                                var layout = {
                                                    xaxis: {
                                                        title: 'score'
                                                    },
                                                    yaxis: {
                                                        title: 'percentage of successful attempts',
                                                        tickformat: '%'
                                                    },
                                                    showlegend: false
                                                };
                                                Plotly.plot(SCORECHART, [data], layout, {staticPlot: true});
                                            </script>
                                        </li>
                                        <li class="list-group-item">
                                            Cumulative completion time graph:
                                            <div id="timechart" style="width:700px;height:500px;"></div>
                                            <script type="text/javascript">
                                                var TIMECHART = document.getElementById('timechart');
                                                var ydata = [];
                                                var xdata = [];
                                                @for($j = count($statistics['times']) - 1; $j >= 0; $j--)
                                                ydata.push({{($j + 1) / count($statistics['times'])}});
                                                xdata.push({{$statistics['times'][$j]->problemtime}});
                                                @endfor
                                                ydata.push(0);
                                                xdata.push(0);
                                                var data = {
                                                    x: xdata,
                                                    y: ydata,
                                                    type: 'scatter',
                                                    fill: 'tozeroy'
                                                };
                                                var layout = {
                                                    xaxis: {
                                                        title: 'completion time in seconds'
                                                    },
                                                    yaxis: {
                                                        title: 'percentage of successful attempts',
                                                        tickformat: '%'
                                                    },
                                                    showlegend: false
                                                };
                                                Plotly.plot(TIMECHART, [data], layout, {staticPlot: true});
                                            </script>
                                        </li>

                                    </ul>
                                    </p>
                                </div>
                                {{-- Show the teacher a list of mails of people who have completed the problem --}}
                                <div id="completed" class="tab-pane fade">
                                    <h3>Students who have completed the problem</h3>
                                    <p>
                                    <table class="table">
                                        <thead>
                                        <tr>
                                            <th scope="col">#</th>
                                            <th scope="col">Email</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        @php
                                            {{$i = 0;}}
                                        @endphp
                                        @foreach($statistics['completedmails'] as $compl)
                                            <tr>
                                                <th scope="row">{{++$i}}</th>
                                                <td>{{$compl->email}}</td>
                                            </tr>
                                        @endforeach
                                        </tbody>
                                    </table>
                                    </p>
                                </div>
                            @endif
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
