/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.routing.jgrapht;

import java.util.Arrays;
import java.util.HashSet;
import org.jgrapht.Graph;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.opentcs.components.kernel.routing.Edge;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.strategies.basic.routing.edgeevaluator.EdgeEvaluatorComposite;

/**
 */
public class DefaultModelGraphMapperTest {

  private Point pointA;
  private Point pointB;
  private Point pointC;
  private Point pointD;

  private Path pathAB;
  private Path pathBC;
  private Path pathCD;
  private Path pathAD;

  private Vehicle vehicle;

  private EdgeEvaluatorComposite evaluator;
  private ShortestPathConfiguration configuration;
  private DefaultModelGraphMapper mapper;

  @BeforeEach
  public void setUp() {
    pointA = new Point("A");
    pointB = new Point("B");
    pointC = new Point("C");
    pointD = new Point("D");

    pathAB = new Path("A-->B", pointA.getReference(), pointB.getReference())
        .withMaxVelocity(1000)
        .withMaxReverseVelocity(0);
    pathBC = new Path("B-->C", pointB.getReference(), pointC.getReference())
        .withMaxVelocity(1000)
        .withMaxReverseVelocity(0);
    pathCD = new Path("C-->D", pointC.getReference(), pointD.getReference())
        .withMaxVelocity(1000)
        .withMaxReverseVelocity(0);
    pathAD = new Path("A<->D", pointA.getReference(), pointD.getReference())
        .withMaxVelocity(1000)
        .withMaxReverseVelocity(1000);

    vehicle = new Vehicle("someVehicle");

    evaluator = mock(EdgeEvaluatorComposite.class);
    configuration = mock(ShortestPathConfiguration.class);
    mapper = new DefaultModelGraphMapper(evaluator, configuration);
  }

  @Test
  public void createEmptyGraph() {
    when(configuration.algorithm()).thenReturn(ShortestPathConfiguration.Algorithm.DIJKSTRA);

    Graph<String, Edge> graph = mapper.translateModel(new HashSet<>(),
                                                      new HashSet<>(),
                                                      vehicle);
    assertEquals(0, graph.vertexSet().size());
    assertEquals(0, graph.edgeSet().size());
    verify(evaluator).onGraphComputationStart(vehicle);
    verify(evaluator).onGraphComputationEnd(vehicle);
  }

  @Test
  public void createGraphWithFourPointsAndNoPath() {
    when(configuration.algorithm()).thenReturn(ShortestPathConfiguration.Algorithm.DIJKSTRA);

    Graph<String, Edge> graph
        = mapper.translateModel(new HashSet<>(Arrays.asList(pointA, pointB, pointC, pointD)),
                                new HashSet<>(),
                                vehicle);

    assertEquals(4, graph.vertexSet().size());
    assertTrue(graph.vertexSet().contains(pointA.getName()));
    assertTrue(graph.vertexSet().contains(pointB.getName()));
    assertTrue(graph.vertexSet().contains(pointC.getName()));
    assertTrue(graph.vertexSet().contains(pointD.getName()));
    assertEquals(0, graph.edgeSet().size());
    verify(evaluator).onGraphComputationStart(vehicle);
    verify(evaluator).onGraphComputationEnd(vehicle);
  }

  @Test
  public void createGraphWithFourPointsAndOneUnidirectionalPath() {
    when(configuration.algorithm()).thenReturn(ShortestPathConfiguration.Algorithm.DIJKSTRA);

    Graph<String, Edge> graph
        = mapper.translateModel(new HashSet<>(Arrays.asList(pointA, pointB, pointC, pointD)),
                                new HashSet<>(Arrays.asList(pathAB)),
                                vehicle);

    assertEquals(4, graph.vertexSet().size());
    assertTrue(graph.vertexSet().contains(pointA.getName()));
    assertTrue(graph.vertexSet().contains(pointB.getName()));
    assertTrue(graph.vertexSet().contains(pointC.getName()));
    assertTrue(graph.vertexSet().contains(pointD.getName()));
    assertEquals(1, graph.edgeSet().size());
    assertEquals(1,
                 graph.edgeSet().stream()
                     .filter(edge -> edge.getPath().getName().equals(pathAB.getName()))
                     .filter(edge -> !edge.isTravellingReverse())
                     .count());
    verify(evaluator).onGraphComputationStart(vehicle);
    verify(evaluator).onGraphComputationEnd(vehicle);
  }

  @Test
  public void createGraphWithFourPointsAndOneBidirectionalPath() {
    when(configuration.algorithm()).thenReturn(ShortestPathConfiguration.Algorithm.DIJKSTRA);

    Graph<String, Edge> graph
        = mapper.translateModel(new HashSet<>(Arrays.asList(pointA, pointB, pointC, pointD)),
                                new HashSet<>(Arrays.asList(pathAD)),
                                vehicle);

    assertEquals(4, graph.vertexSet().size());
    assertTrue(graph.vertexSet().contains(pointA.getName()));
    assertTrue(graph.vertexSet().contains(pointB.getName()));
    assertTrue(graph.vertexSet().contains(pointC.getName()));
    assertTrue(graph.vertexSet().contains(pointD.getName()));
    assertEquals(2, graph.edgeSet().size());
    assertEquals(1,
                 graph.edgeSet().stream()
                     .filter(edge -> edge.getPath().getName().equals(pathAD.getName()))
                     .filter(edge -> !edge.isTravellingReverse())
                     .count());
    assertEquals(1,
                 graph.edgeSet().stream()
                     .filter(edge -> edge.getPath().getName().equals(pathAD.getName()))
                     .filter(edge -> edge.isTravellingReverse())
                     .count());
    verify(evaluator).onGraphComputationStart(vehicle);
    verify(evaluator).onGraphComputationEnd(vehicle);
  }

  @Test
  public void createGraphWithFourPointsThreeUnidirectionalAndOneBidirectionalPaths() {
    when(configuration.algorithm()).thenReturn(ShortestPathConfiguration.Algorithm.DIJKSTRA);

    Graph<String, Edge> graph
        = mapper.translateModel(new HashSet<>(Arrays.asList(pointA, pointB, pointC, pointD)),
                                new HashSet<>(Arrays.asList(pathAB, pathBC, pathCD, pathAD)),
                                vehicle);

    assertEquals(4, graph.vertexSet().size());
    assertTrue(graph.vertexSet().contains(pointA.getName()));
    assertTrue(graph.vertexSet().contains(pointB.getName()));
    assertTrue(graph.vertexSet().contains(pointC.getName()));
    assertTrue(graph.vertexSet().contains(pointD.getName()));
    assertEquals(5, graph.edgeSet().size());
    assertEquals(1,
                 graph.edgeSet().stream()
                     .filter(edge -> edge.getPath().getName().equals(pathAB.getName()))
                     .filter(edge -> !edge.isTravellingReverse())
                     .count());
    assertEquals(1,
                 graph.edgeSet().stream()
                     .filter(edge -> edge.getPath().getName().equals(pathBC.getName()))
                     .filter(edge -> !edge.isTravellingReverse())
                     .count());
    assertEquals(1,
                 graph.edgeSet().stream()
                     .filter(edge -> edge.getPath().getName().equals(pathCD.getName()))
                     .filter(edge -> !edge.isTravellingReverse())
                     .count());
    assertEquals(1,
                 graph.edgeSet().stream()
                     .filter(edge -> edge.getPath().getName().equals(pathAD.getName()))
                     .filter(edge -> !edge.isTravellingReverse())
                     .count());
    assertEquals(1,
                 graph.edgeSet().stream()
                     .filter(edge -> edge.getPath().getName().equals(pathAD.getName()))
                     .filter(edge -> edge.isTravellingReverse())
                     .count());
    verify(evaluator).onGraphComputationStart(vehicle);
    verify(evaluator).onGraphComputationEnd(vehicle);
  }

}
