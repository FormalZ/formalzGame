{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.app')

@section('content')
    <div class="container">
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <div class="panel panel-default">
                    <div class="panel-heading">Edit problem</div>

                    <div class="panel-body">
                        <form class="form-horizontal" method="POST"
                              action="{{ route('problem.edit', ['id' => $problem->id]) }}">
                            {{ csrf_field() }}

                            <div class="form-group{{ $errors->has('header') ? ' has-error' : '' }}">
                                <label for="header" class="col-md-4 control-label">Problem Header</label>

                                <div class="col-md-6">
                                    <input id="header" type="text" class="form-control" name="header"
                                           value="{{ $problem->header }}" required autofocus>

                                    @if ($errors->has('header'))
                                        <span class="help-block">
                                        <strong>{{ $errors->first('header') }}</strong>
                                    </span>
                                    @endif
                                    <div class="custom-control-label">In the header, make sure to declare the types of
                                        <b>EVERY</b> variable in the problem!
                                    </div>
                                </div>
                            </div>

                            <div class="form-group{{ $errors->has('description') ? ' has-error' : '' }}">
                                <label for="description" class="col-md-4 control-label">Problem Description</label>

                                <div class="col-md-6">
                                <textarea id="description" class="form-control" name="description" cols="75" rows="5"
                                          required>{{ $problem->description }}</textarea>

                                    @if ($errors->has('description'))
                                        <span class="help-block">
                                        <strong>{{ $errors->first('description') }}</strong>
                                    </span>
                                    @endif
                                </div>
                            </div>

                            <div class="form-group{{ $errors->has('pre_conditions') ? ' has-error' : '' }}">
                                <label for="pre_conditions" class="col-md-4 control-label">Preconditions</label>

                                <div class="col-md-6">
                                <textarea id="pre_conditions" class="form-control" name="pre_conditions" cols="75"
                                          rows="3" required>{{ $problem->pre_conditions }}</textarea>
                                    @if ($errors->has('pre_conditions'))
                                        <span class="help-block">
                                        <strong>{{ $errors->first('pre_conditions') }}</strong>
                                    </span>
                                    @endif
                                </div>
                            </div>

                            <div class="form-group{{ $errors->has('post_conditions') ? ' has-error' : '' }}">
                                <label for="post_conditions" class="col-md-4 control-label">Postconditions</label>

                                <div class="col-md-6">
                                <textarea id="post_conditions" class="form-control" name="post_conditions" cols="75"
                                          rows="3" required>{{ $problem->post_conditions }}</textarea>
                                    @if ($errors->has('post_conditions'))
                                        <span class="help-block">
                                        <strong>{{ $errors->first('post_conditions') }}</strong>
                                    </span>
                                    @endif
                                </div>
                            </div>

                            <div class="form-group{{ $errors->has('difficulty') ? ' has-error' : '' }}">
                                <label for="difficulty" class="col-md-4 control-label">Difficulty (1 - 5)</label>

                                <div class="col-md-6">
                                    <input id="difficulty" type="number" max="5" min="1" class="form-control"
                                           name="difficulty" value="{{ $problem->difficulty }}" required>
                                    <a href={{route('teacher.examples')}} class="list-group-item">Examples for different
                                        difficulties</a>

                                    @if ($errors->has('difficulty'))
                                        <span class="help-block">
                                        <strong>{{ $errors->first('difficulty') }}</strong>
                                    </span>
                                    @endif
                                </div>
                            </div>


                            <div class="form-group{{ $errors->has('problemcount') ? ' has-error' : '' }}">
                                <label for="problemcount" class="col-md-4 control-label">Amount of intermediate problems
                                    (0 - 10)</label>

                                <div class="col-md-6">
                                    <input id="problemcount" type="number" max="10" min="0" class="form-control"
                                           name="problemcount" value="{{ $problem->problemcount }}" required>

                                    @if ($errors->has('problemcount'))
                                        <span class="help-block">
                                        <strong>{{ $errors->first('problemcount') }}</strong>
                                    </span>
                                    @endif
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-4">
                                    <button class="btn btn-primary" type="button" data-toggle="collapse"
                                            data-target="#advancedOptions" aria-expanded="false"
                                            aria-controls="advancedOptions">Advanced options
                                    </button>
                                </div>
                            </div>
                            <div id="advancedOptions" class="collapse">
                                <div class="form-group{{ $errors->has('lives') ? ' has-error' : '' }}">
                                    <label for="lives" class="col-md-4 control-label">Amount of lives (50-500)</label>

                                    <div class="col-md-6">
                                        <input id="lives" type="number" max="500" min="50" class="form-control"
                                               name="lives" value="{{ $problem->lives }}" placeholder="100">

                                        @if ($errors->has('lives'))
                                            <span class="help-block">
                                        <strong>{{ $errors->first('lives') }}</strong>
                                    </span>
                                        @endif
                                    </div>
                                </div>

                                <div class="form-group{{ $errors->has('money') ? ' has-error' : '' }}">
                                    <label for="money" class="col-md-4 control-label">Amount of money
                                        (2500-10000)</label>

                                    <div class="col-md-6">
                                        <input id="money" type="number" max="10000" min="2500" class="form-control"
                                               name="money" value="{{ $problem->money }}" placeholder="5000">

                                        @if ($errors->has('money'))
                                            <span class="help-block">
                                        <strong>{{ $errors->first('money') }}</strong>
                                    </span>
                                        @endif
                                    </div>
                                </div>

                                <div class="form-group{{ $errors->has('deadline') ? ' has-error' : '' }}">
                                    <label for="deadline" class="col-md-4 control-label">Deadline</label>

                                    <div class="col-md-6">
                                        @if ($problem->deadline > 0)
                                            <input id="deadline" type="datetime-local" class="form-control"
                                                   name="deadline"
                                                   value="{{ date('Y-m-d',$problem->deadline) }}T{{date('H:i', $problem->deadline)}}">
                                        @else
                                            <input id="deadline" type="datetime-local" class="form-control"
                                                   name="deadline">
                                        @endif
                                        @if ($errors->has('deadline'))
                                            <span class="help-block">
                                        <strong>{{ $errors->first('deadline') }}</strong>
                                    </span>
                                        @endif
                                    </div>
                                </div>

                                <div class="form-group{{ $errors->has('displayDate') ? ' has-error' : '' }}">
                                    <label for="displayDate" class="col-md-4 control-label">Visible from</label>

                                    <div class="col-md-6">
                                        @if ($problem->displayDate > 0)
                                            <input id="displayDate" type="datetime-local" class="form-control"
                                                   name="displayDate"
                                                   value="{{ date('Y-m-d',$problem->displayDate) }}T{{date('H:i', $problem->displayDate)}}">
                                        @else
                                            <input id="displayDate" type="datetime-local" class="form-control"
                                                   name="displayDate">
                                        @endif
                                        @if ($errors->has('displayDate'))
                                            <span class="help-block">
                                        <strong>{{ $errors->first('displayDate') }}</strong>
                                    </span>
                                        @endif
                                    </div>
                                </div>

                                <div class="form-group{{ $errors->has('autohide') ? ' has-error' : '' }}">
                                    <label for="autohide" class="col-md-4 control-label">Autohide after deadline</label>

                                    <div class="col-md-6">
                                        @if ($problem->autohide)
                                            <input id="autohide" type="checkbox" class="form-control" name="autohide"
                                                   value="autohide" checked>
                                        @else
                                            <input id="autohide" type="checkbox" class="form-control" name="autohide"
                                                   value="autohide">
                                        @endif

                                        @if ($errors->has('linkinvite'))
                                            <span class="help-block">
                                        <strong>{{ $errors->first('linkinvite') }}</strong>
                                    </span>
                                        @endif
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-md-6 col-md-offset-4">
                                    <button type="submit" class="btn btn-primary">
                                        Save Changes
                                    </button>
                                    <button type="button"
                                            onclick="window.location.href='/problem/{{$problem->id}}/delete'">
                                        Delete this problem
                                    </button>
                                    <button type="button"
                                            onclick="window.location.href='/problem/{{$problem->id}}'">
                                        Back to problem
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
